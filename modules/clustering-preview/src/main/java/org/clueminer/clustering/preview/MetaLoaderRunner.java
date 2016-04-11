package org.clueminer.clustering.preview;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.colors.PaletteGenerator;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.io.CsvLoader;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class MetaLoaderRunner implements Runnable {

    private File[] files = null;
    private Preferences pref;
    private ProgressHandle ph;
    private Dataset<? extends Instance>[] result;
    private final static Logger logger = Logger.getLogger(MetaLoaderRunner.class.getName());
    private final Map<Integer, Color> colors = new HashMap<>();
    private Color[] baseColors = {Color.red, Color.green, Color.blue, Color.MAGENTA, Color.DARK_GRAY, Color.pink, Color.CYAN};

    public MetaLoaderRunner(File[] files, Preferences pref, ProgressHandle ph, Dataset<? extends Instance>[] result) {
        this.files = files;
        this.pref = pref;
        this.ph = ph;
        this.result = result;
    }

    public MetaLoaderRunner() {

    }

    @Override
    public void run() {
        ph.start(0);
        int i = 0;
        for (File file : files) {
            if (file.exists()) {
                try {
                    result[i] = loadMTimeseries(file);
                    assignColours(result[i]);
                    i++;
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                System.err.println("file '" + file.getAbsolutePath() + "' does not exist");
            }
        }
    }

    protected Dataset<? extends Instance> loadMTimeseries(File file) throws FileNotFoundException, IOException {
        char separator = ',';
        Dataset<? extends Instance> dataset = new TimeseriesDataset<>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> metaAttr = new ArrayList<>();
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
        loader.load(file, d);
        return dataset;
    }

    protected Map<Integer, Color> assignColours(Dataset<? extends Instance> data) {
        double[] meta;
        int pk;
        Color col;
        //ColorGenerator gen = (ColorGenerator) new PaletteGenerator();
        ColorGenerator gen = new PaletteGenerator();
        int i = 0;
        for (Instance inst : data) {
            meta = inst.getMetaNum();
            pk = (int) meta[1]; //we can safely cast to integer
            if (colors.containsKey(pk)) {
                col = colors.get(pk);
            } else {
                col = gen.next(baseColors[(i++) % baseColors.length]);
                colors.put(pk, col);
            }
            inst.setColor(col);
        }
        return colors;
    }

    public Dataset<? extends Instance>[] getResult() {
        return result;
    }

    public Map<Integer, Color> getColors() {
        return colors;
    }

}
