package IOTDevices.DirMonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

import static java.lang.Thread.sleep;

public class TestMonitor {
    public static void main(String[] args) throws InterruptedException, IOException {
        DirMonitor dirMonitor = new DirMonitor("/home/shanikedem/Music");
        LogerObserver firstObserver = new LogerObserver("/home/shanikedem/Music/logger.txt", "myfile.txt", "/home/shanikedem/Music/myfile.txt");
        dirMonitor.register(firstObserver);
        dirMonitor.start();
        sleep(30000);

        System.out.println("baybay!!");
        dirMonitor.stop();
        //dirMonitor.unregister(firstObserver);


        sleep(30000);
        System.out.println("hay hay!!");
        //dirMonitor.register(firstObserver);
        dirMonitor.start();




    }
}

class LogerObserver implements Observer{
    private String logfile = null;
    private String observedFile = null;
    private String filePath = null;
    private FileCrud<Integer, String> fileCrud = null;
    private int numLines = 0;


    public LogerObserver(String logfile, String observedFile, String filePath) {
        this.logfile = logfile;
        this.observedFile = observedFile;
        fileCrud = new FileCrud(this.logfile);
        this.filePath = filePath;
        System.out.println("ctor observer monitorFile: " + this.observedFile);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (!(arg instanceof ObserverMessage)) {
            return;
        }

        ObserverMessage message = (ObserverMessage)arg;

        if(message.getFileName().equals(observedFile)){
            switch (message.getEvent()){
                case CREATE:
                    String record = getRecord();
                    System.out.println("update in "  + observedFile + ": create new lines: " + fileCrud.create(record));
                    break;
                case DELETE:
                    fileCrud.delete(1);
                    break;
                case MODIFY:
                    fileCrud.update("1");
                    break;
            }
        }
    }

    private String getRecord(){

        String record = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (int i = 0; i < numLines; i++) {
               br.readLine(); // skip the lines before the start line
            }
            String line;

            while ((line = br.readLine()) != null) {
                record = record + line + "\n";
                ++numLines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return record;
    }
}