package org.clueminer.export.sorting;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class SortingRunner implements Runnable {

    private File file;
    private ProgressHandle ph;
    private boolean includeHeader;
    private Object2DoubleOpenHashMap<String> results;
    private Dataset<? extends Instance> dataset;
    private DecimalFormat df;

    public SortingRunner(File file, Dataset<? extends Instance> dataset, Object2DoubleOpenHashMap<String> results, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.ph = ph;
        this.dataset = dataset;
        this.results = results;
        df = initFormat(3);
        parsePref(pref);
    }

    private void parsePref(Preferences pref) {
        includeHeader = pref.getBoolean(SortingOptions.INCLUDE_HEADER, true);
    }

    private DecimalFormat initFormat(int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        return format;
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            StringBuilder sb;
            int cnt = 0;
            if (ph != null) {
                ph.start(results.size());
            }
            if (includeHeader) {
                sb = new StringBuilder();
                int j = 0;
                for (String s : results.keySet()) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(s);
                    j++;
                }
                sb.append("\n");
                fw.write(sb.toString());
            }
            sb = new StringBuilder();
            int i = 0;
            for (Double score : results.values()) {

                if (i > 0) {
                        sb.append(",");
                    }
                sb.append(df.format(df));
                if (ph != null) {
                    ph.progress(cnt++);
                }
            }
            sb.append(",").append(dataset.getName());
            sb.append("\n");
            fw.write(sb.toString());

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
