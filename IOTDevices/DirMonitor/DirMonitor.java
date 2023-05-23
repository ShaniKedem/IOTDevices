package IOTDevices.DirMonitor;

import java.io.IOException;
import java.nio.file.*;
import java.util.Observable;
import java.util.Observer;

public class DirMonitor {
    private WatchService watchService = null;
    private final observableImpl observable = new observableImpl();
    private boolean keepMonitor = false;
    private final Object synchLock = new Object();

    private static class observableImpl extends Observable {
        private void setChangedImpl(){
            setChanged();
        }

    }
    public DirMonitor(String path){
        initWatchService(path);
    }

    public void register(Observer observerImp){
        synchronized (synchLock){
            System.out.println("register!");
            observable.addObserver(observerImp);
        }
    }
    public void unregister(Observer observerImp){
        synchronized (synchLock){
            observable.deleteObserver(observerImp);
        }
    }

    public void start(){
        System.out.println("start!");
        synchronized (synchLock) {
            if (!keepMonitor) {
                keepMonitor = true;
                Thread monitorThread = new Thread(new MonitorThread());
                monitorThread.start();
            }
        }

    }
    public void stop() {
        synchronized (synchLock) {
            keepMonitor = false;
        }
    }

    public void close() throws IOException {
        synchronized (synchLock){
            watchService.close();
        }
    }


    private class MonitorThread implements Runnable {
        @Override
        public void run() {
            WatchKey key = null;
            ObserverMessage message = null;
            System.out.println("monitor thread is running");

            while (keepMonitor)
            {
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    System.out.println("monitor fail InterruptedException");
                    return;
                }

                if (key != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path filePath = (Path) event.context();
                        EnumEvent enumEvent = null;
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            enumEvent = EnumEvent.CREATE;

                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            enumEvent = EnumEvent.DELETE;

                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            enumEvent = EnumEvent.MODIFY;
                        }

                        message =  new ObserverMessage(filePath.toString(), enumEvent);
                        Broadcast(message);

                    }
                    key.reset();
                }

            }

        }

    }

    private void initWatchService(String path){
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path dirPath = Paths.get(path);
            dirPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        }
        catch (IOException e)
        {
            System.out.println("initWatchService failed");
            throw new RuntimeException();
        }

    }

    private void Broadcast(ObserverMessage message){
        synchronized (synchLock) {
            observable.setChangedImpl();
            observable.notifyObservers((Object) message);
        }
    }

}