package IOTDevices.DirMonitor;

import java.io.FileWriter;
import java.io.IOException;
import IOTDevices.*;

public class FileCrud<ID, T> implements ICrud<Integer,T> {
    String logfile = null;

    public FileCrud(String logfile) {
        this.logfile = logfile;
    }

    @Override
    public Integer create(T record) {
        try (FileWriter fw = new FileWriter(logfile, true)) {
            fw.write((String) record); // write the text to the end of the file
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] lines = ((String)record).split("\n");
        return lines.length;
    }

    @Override
    public T read(Integer id) {
        throw new RuntimeException("Not supported in this case");
    }

    @Override
    public Integer update(T record) {
        throw new RuntimeException("Not supported in this case");
    }

    @Override
    public void delete(Integer id) {
        throw new RuntimeException("Not supported in this case");
    }
}
