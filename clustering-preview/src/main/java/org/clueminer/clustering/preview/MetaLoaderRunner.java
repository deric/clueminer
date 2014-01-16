package org.clueminer.clustering.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.io.CsvLoader;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class MetaLoaderRunner implements Runnable {

    private final File[] files;
    private final Preferences pref;
    private final ProgressHandle ph;
    private final Dataset<? extends Instance>[] result;
    private final static Logger logger = Logger.getLogger(MetaLoaderRunner.class.getName());

    public MetaLoaderRunner(File[] files, Preferences pref, ProgressHandle ph, Dataset<? extends Instance>[] result) {
        this.files = files;
        this.pref = pref;
        this.ph = ph;
        this.result = result;
    }

    @Override
    public void run() {
        ph.start(0);
        System.out.println("loading meta data...");
        int i = 0;
        for (File file : files) {
            if (file.exists()) {
                try {
                    System.out.println("processing " + file.getAbsolutePath());
                    result[i++] = loadMTimeseries(file);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                System.err.println("file '" + file.getAbsolutePath() + "' does not exist");
            }
        }
    }

    private Dataset<? extends Instance> loadMTimeseries(File file) throws FileNotFoundException, IOException {
        char separator = ',';
        Dataset<? extends Instance> dataset = new TimeseriesDataset<ContinuousInstance>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> metaAttr = new ArrayList<Integer>();
        //skipped.add(0); //first one is ID
        loader.setClassIndex(0);
        for (int i = 1; i < 7; i++) {
            metaAttr.add(i);
        }
        for (int j = 0; j < 7; j++) {
            loader.addNameAttr(j); //meta attributes
        }
        loader.setNameJoinChar(", ");

        String[] firstLine = CsvLoader.firstLine(file, String.valueOf(separator));
        int i = 0;
        int index;
        int last = firstLine.length;
        int offset = metaAttr.size() + 1;//class attr
        TimePoint tp[] = new TimePointAttribute[last - offset];
        logger.log(Level.INFO, "time attrs: {0}", last);
        logger.log(Level.INFO, "time series attrs: {0}", tp.length);
        double pos;
        int cnt = 0;
        for (String item : firstLine) {
            if (i >= offset) {
                index = i - offset;
                pos = Double.valueOf(item);
                tp[cnt++] = new TimePointAttribute(index, index, pos);
            }
            i++;
        }
        ((TimeseriesDataset<ContinuousInstance>) dataset).setTimePoints(tp);
        loader.setMetaAttr(metaAttr);
        loader.setSeparator(separator);
        //loader.setClassIndex(0);
        loader.setSkipHeader(true);
        Dataset<Instance> d = (Dataset<Instance>) dataset;
        loader.setDataset(d);
        loader.load(file);
        return dataset;
    }

    public Dataset<? extends Instance>[] getResult() {
        return result;
    }

}
