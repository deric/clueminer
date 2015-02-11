package org.clueminer.export.sorting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Clustering;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class SortingRunner implements Runnable {

    private final File file;
    private ProgressHandle ph;
    private boolean includeHeader;
    private DecimalFormat df;
    private final SortingExporter exp;
    private char separator = ',';

    public SortingRunner(File file, SortingExporter exp, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.ph = ph;
        this.exp = exp;
        df = initFormat(3);
        parsePref(pref);
    }

    private void parsePref(Preferences pref) {
        includeHeader = pref.getBoolean(SortingOptions.INCLUDE_HEADER, true);
    }

    private DecimalFormat initFormat(int d) {
        DecimalFormat format = new DecimalFormat("#.###");
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
            StringBuilder h, sb;
            int cnt = 0;
            if (ph != null) {
                ph.start(exp.getResults().size());
            }
            sb = new StringBuilder();
            h = new StringBuilder();
            int i = 0;
            for (Map.Entry<String, Double> score : exp.getResults().entrySet()) {
                if (i > 0) {
                    sb.append(",");
                    h.append(separator);
                }
                h.append(score.getKey());
                sb.append(df.format(score.getValue()));
                if (ph != null) {
                    ph.progress(cnt++);
                }
                i++;
            }

            if (includeHeader) {
                h.append(separator).append("dataset");
                h.append(separator).append("clusterings");
                h.append(separator).append("reference");
                h.append("\n");
                fw.write(h.toString());
            }

            sb.append(separator).append(exp.getDataset().getName());
            sb.append(separator).append(exp.getClusterings().size());
            sb.append(separator).append(exp.getEvaluator().getName());
            sb.append("\n");
            fw.write(sb.toString());

            fw.write("\n");

            for (Clustering c : exp.getClusterings()) {
                sb = new StringBuilder();
                sb.append(c.getEvaluationTable().getScore(exp.getEvaluator()));
                sb.append(separator);
                sb.append(c.fingerprint());
                sb.append(separator);
                sb.append("\n");
                fw.write(sb.toString());
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
