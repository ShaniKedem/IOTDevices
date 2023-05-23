/* ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ *
~ Author        <Shani Kedemn>                                   *
~ Date          <2.03.2023>                                      ~
* Reviewer      <Racheli Seliger>                                        *
~ Description   <My Thread Pool>                                 ~
* Group         FS133                                            *
~ Company       ILRD Ramat Gan                                   ~
* ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ * ~ */

package IOTDevices.ThreadPool;

import IOTDevices.WaitablePQueue.*;

//import sun.awt.Mutex;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class ThreadPool<V> implements Executor {

    private final List<Thread> threadList = new LinkedList<>();
    private final Collection<Thread> syncThreadList = Collections.synchronizedList(threadList);
    private final WaitablePQueue<Task> taskPQ = new WaitablePQueue<>();
    private final HashMap<Task, Thread> activeTaskMap = new HashMap<>();
    private final Object pauseLock = new Object();
    private final Object awaitLock = new Object();
    private final Object cancelMutex = new Object();
    private boolean isShutdown = false;
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private int numberOfThreads = 0;
    /*-------------------------------------------------------------------------*/

    public ThreadPool(int numOfThreads){
        setNumOfThreads(numOfThreads);
    }
    /*-------------------------------------------------------------------------*/

    @Override
    public void execute(Runnable runnable) {
        this.submit(runnable, Priority.MEDIUM);
    }

    //this submit convert Runnable to Callable
    public Future<Void> submit(Runnable runnable, Priority priority){
        return submit(Executors.callable(runnable, null), priority);

    };

    //this submit convert Runnable to Callable
    public <V>Future<V> submit(Runnable runnable, Priority priority, V value){
        return submit(Executors.callable(runnable, value), priority);
    };

    public <V>Future<V> submit(Callable<V> callable){
        return submit(callable, Priority.MEDIUM);
    };

    public <V>Future<V> submit(Callable<V> callable, Priority priority){
        if(isShutdown)
        {
            throw new RejectedExecutionException();
        }
        Task task = new Task<>(callable, priority.getValue());
        taskPQ.enqueue(task);
        return task.getFuture();
    };

    public void setNumOfThreads(int numThreads){
        int difference = numberOfThreads - numThreads;

        if(0 < difference)
        {
            Callable stopRun = makeStopCallable();
            Task taskStopRun = new Task<>(stopRun, 5);
            for(int i = 0; i < difference; ++i)
            {
                taskPQ.enqueue(taskStopRun);
            }
        }
        else {
            difference = -difference;
            for(int i = 0; i < difference; ++i)
            {
                WorkerThread newWorker = new WorkerThread();
                syncThreadList.add(newWorker);
                newWorker.start();
            }
        }
        this.numberOfThreads = numThreads;
    };

    public void pause(){
        isPaused.set(true);
    };

    public void resume(){
        isPaused.set(false);
        synchronized (pauseLock){
            this.pauseLock.notifyAll();
        }
    };

    public void shutdown(){
        isShutdown = true;
        if (isPaused.get())
        {
            resume();
        }
        Callable stopRun = makeStopCallable();
        Task taskShutdown = new Task<>(stopRun, 0);
        for(int i = 0; i < numberOfThreads; ++i)
        {
            taskPQ.enqueue(taskShutdown);
        }
    };

    private Callable makeStopCallable(){
        Callable stopRun = new Callable() {
            @Override
            public Object call() throws Exception {
                Thread curThread = Thread.currentThread();
                ((WorkerThread)curThread).setKeepRunning(false);
                return null;
            };
        };
        return stopRun;
    };

    public void awaitTermination() throws InterruptedException {

        while (!syncThreadList.isEmpty())
        {
            synchronized (awaitLock){
                awaitLock.wait();
            }
        }


    };

    /*-----------------------------------------------------------------------------*/

    private class Task<V> implements Comparable<Task>{

        private Callable<V> callable = null;
        private int priority = 0;
        private Future<V> future = null;

        private Task(Callable callable, int priority){
            this.callable = callable;
            this.priority = priority;
            this.future = new Taskfuture(this);
        }

        private int getPriority() {
            return priority;
        }

        private Future getFuture(){
            return this.future;
        };
        private Callable getCallable(){
            return callable;
        };


        @Override
        public int compareTo(Task task) {
            return task.getPriority() - this.priority;
        }

        /*-----------------------------------------------------------------------------*/

        private class Taskfuture implements Future<V>{
            private V result = null;
            private boolean isCancelled = false;
            private boolean isDone;
            private Task<V> myTask = null;
            private final Object futureLock;
            private Semaphore getValueSem = new Semaphore(0);

            private Taskfuture(Task<V> myTask){
                this.myTask = myTask;
                futureLock = new Object();
            }

            public boolean cancel(boolean mayInterruptIfRunning) {

                if(taskPQ.remove(myTask))
                {
                    isCancelled = true;
                }
                else if(mayInterruptIfRunning && (null != activeTaskMap.get(myTask)))
                {
                	synchronized(cancelMutex) {
	                    Thread thread = activeTaskMap.get(myTask);
	                    thread.interrupt();
	                    isCancelled = true;
                	}
                }
                this.setDone(!isCancelled);
                return isCancelled;
            }

            @Override
            public boolean isCancelled() {
                return isCancelled;
            }

            @Override
            public boolean isDone() {
                return isDone;
            }

            @Override
            public V get() throws InterruptedException, ExecutionException {
                if(!isCancelled)
                {
                    getValueSem.acquire();
                }
                return result;

            }

            @Override
            public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                if(!getValueSem.tryAcquire(l, timeUnit)){
                   throw new TimeoutException();
                }
                return result;
            }

            private void setResult(V result){
                this.result = result;
            }

            private void setDone(boolean done) {
                getValueSem.release();
                isDone = done;
            }
        }
    }

    /*-----------------------------------------------------------------------------*/

    private class WorkerThread extends Thread{

        V result = null;
        boolean keepRunning = true;
        Task task = null;
        Callable callable = null;
        Future future = null;

        public WorkerThread() {
        }

        public void setKeepRunning(boolean keepRunning) {
            this.keepRunning = keepRunning;
        }

        @Override
        public void run() {
            while (keepRunning)
            {
                task = taskPQ.dequeue();
                checkPause();
                activeTaskMap.put(task, this);

                try {
                    callable = task.getCallable();
                    result = (V) callable.call();
                    synchronized(cancelMutex) {
                    	activeTaskMap.remove(task);
                    }
                } catch (InterruptedException e) {
                    continue;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                future = task.getFuture();
                ((Task.Taskfuture)future).setResult(result);
                ((Task.Taskfuture)future).setDone(true);

            }
            //after stopRun
            syncThreadList.remove(this);
            synchronized (awaitLock)
            {
                awaitLock.notify();
            }
        }

        private void checkPause(){
            if (isPaused.get())
            {
                synchronized (pauseLock) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

}




