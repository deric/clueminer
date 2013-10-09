package org.clueminer.mlearn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.io.FileHandler;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.utils.progress.ProgressTicket;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;


/**
 *
 * @author Tomas Barton
 */
public class MLearnImporter implements LongTask, Runnable {

    private File file;
    private int timesCount = 0;
    private int workUnits = 0;
    private ProgressHandle ph;
    private Dataset<Instance> dataset;

    public MLearnImporter(File file) {
        this.file = file;
    }

    public MLearnImporter(File file, ProgressHandle ph) {
        this.file = file;
        this.ph = ph;
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            try {
                ph.start(0);
                //it would be nice to know number of lines
                dataset = new SampleDataset<Instance>(100);
                FileHandler.loadDataset(file, dataset);

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                br.close();
                ph.finish();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    private String parseName(File f) {
        String name = f.getName();
        //remove extension
        int pos = name.indexOf('.');
        if (pos != -1) {
            name = name.substring(0, pos);
        }
        //remove folders names, if any before filename
        pos = name.lastIndexOf("/");
        if (pos != -1) {
            name = name.substring(pos, name.length());
        }
        return name;
    }


    public Dataset<Instance> getDataset() {
        return dataset;
    }
}
