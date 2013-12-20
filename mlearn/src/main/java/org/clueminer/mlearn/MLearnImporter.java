package org.clueminer.mlearn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.io.CsvLoader;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.types.TimePoint;
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
    private Dataset<? extends Instance> dataset;

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

    public void loadTimeseries(File file) throws FileNotFoundException {
        String separator = "\t";
        dataset = new TimeseriesDataset<ContinuousInstance>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> skipped = new ArrayList<Integer>();
        skipped.add(0); //first one is ID

        String[] firstLine = firstLine(file, separator);
        int i = 0;
        int index;
        int last = firstLine.length - 1;
        TimePoint tp[] = new TimePointAttribute[last - 1];        
        for (String item : firstLine) {
            if (i > 0 && i < last) {
                index = Integer.valueOf(item.substring(1));
                tp[i - 1] = new TimePointAttribute(index, index, index);                
            }
            i++;
        }
        ((TimeseriesDataset<ContinuousInstance>) dataset).setTimePoints(tp);
        loader.setSkipIndex(skipped);
        loader.setSeparator(separator);
        loader.setClassIndex(last);
        loader.setSkipHeader(true);
        Dataset<Instance> d = (Dataset<Instance>) dataset;
        loader.setDataset(d);
        loader.load(file);
    }

    public void loadMTimeseries(File file) throws FileNotFoundException {
        String separator = ",";
        dataset = new TimeseriesDataset<ContinuousInstance>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> skipped = new ArrayList<Integer>();
        //skipped.add(0); //first one is ID
        for (int i = 1; i < 7; i++) {
            skipped.add(i);
        }

        String[] firstLine = firstLine(file, separator);
        int i = 0;
        int index;
        int last = firstLine.length - 1;
        int offset = 6;
        TimePoint tp[] = new TimePointAttribute[last - offset];
        double pos;
        for (String item : firstLine) {
            if (i > offset) {
                index = i - offset - 1;
                pos = Double.valueOf(item);
                tp[index] = new TimePointAttribute(index, index, pos);
            }
            i++;
        }
        ((TimeseriesDataset<ContinuousInstance>) dataset).setTimePoints(tp);
        loader.setSkipIndex(skipped);
        loader.setSeparator(separator);
        loader.setClassIndex(0);
        loader.setSkipHeader(true);
        Dataset<Instance> d = (Dataset<Instance>) dataset;
        loader.setDataset(d);
        loader.load(file);
    }

    private String[] firstLine(File file, String separator) {
        String[] result = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = br.readLine();
                result = line.split(separator);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                br.close();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    @Override
    public void run() {
        try {
            ph.start();
            //loadTimeseries(file);
            loadMTimeseries(file);
            ph.finish();

            /*     BufferedReader br = null;
             if (ph == null) {
             throw new RuntimeException("Progress handle not set");
             }
             try {
             br = new BufferedReader(new FileReader(file));
             try {
             ph.start(0);
             //it would be nice to know number of lines, but we don't unless
             int numLines = 100;
             //we would read the whole file
             String ext = MLearnImporter.getFileExtension(file.getName());
             if ("arff".equals(ext)) {
             // @TODO run ARFF analyzer
             } else {
             //txt, csv, ...
             Detector detector = new TxtDetect();
             DatasetProperties props = detector.detect(br);

             }

             //
             dataset = new SampleDataset<Instance>(100);
             FileHandler.loadDataset(file, dataset, ",");

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
             }*/
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
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

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }
}
