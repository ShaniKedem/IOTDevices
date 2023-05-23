/*************WaitablePQueue**************
 * Developer:                            *
 * reviewer:                             *
 * version: 1                            *
 ****************************************/
package IOTDevices.WaitablePQueue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WaitablePQueue<T>  {

    private PriorityQueue<T> queue;
    Semaphore sem = new Semaphore(0);
    Object lock = new Object();

    public WaitablePQueue(Comparator<? super T> comparator) {
        queue = new PriorityQueue<>(comparator);
    }

    public WaitablePQueue() {
        queue = new PriorityQueue<>();
    }

    public void enqueue(T element){
    	synchronized(lock) {
        queue.add(element);
    	}
        sem.release();
    }

    public T dequeue(){
        T returnVal = null;
        try {
            sem.acquire();
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }
        synchronized(lock) {
        returnVal = queue.poll();
        }
        return returnVal;
    }

    //timeout in miliseconds
    public T dequeue(int timeout) throws TimeoutException {
        T returnVal = null;
        try {
            if(!sem.tryAcquire(timeout, TimeUnit.MILLISECONDS)){
                throw new TimeoutException();
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }

        synchronized(lock) {
        returnVal = queue.poll();
        }
        return returnVal;
    }

    public boolean remove(T element) {
        boolean isRemoved = false;
        if (sem.tryAcquire()) {
        	synchronized(lock) {
	            isRemoved = queue.remove(element);
	            if(!isRemoved){
	                sem.release();
	            }
        	}
        }

        return isRemoved;
    }
}
