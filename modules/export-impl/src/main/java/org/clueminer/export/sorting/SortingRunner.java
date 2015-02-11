package org.clueminer.export.sorting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
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
            StringBuilder sb;
            int cnt = 0;
            if (ph != null) {
                ph.start(exp.getResults().size());
            }
            if (includeHeader) {
                sb = new StringBuilder();
                int j = 0;
                for (String s : exp.getResults().keySet()) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(s);
                    j++;
                }
                sb.append(",").append("dataset");
                sb.append(",").append("clusterings");
                sb.append(",").append("reference");
                sb.append("\n");
                fw.write(sb.toString());
            }
            sb = new StringBuilder();
            int i = 0;
            for (Double score : exp.getResults().values()) {

                if (i > 0) {
                    sb.append(",");
                }
                sb.append(df.format(score));
                System.out.println("value = " + score);
                if (ph != null) {
                    ph.progress(cnt++);
                }
                i++;
            }
            sb.append(",").append(exp.getDataset().getName());
            sb.append(",").append(exp.getClusterings().size());
            sb.append(",").append(exp.getEvaluator().getName());
            sb.append("\n");
            fw.write(sb.toString());

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
