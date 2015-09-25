package org.clueminer.export.sim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class MatrixRunner implements Runnable {

    private final File file;
    private final ProgressHandle ph;
    private boolean includeHeader;
    private final DendrogramMapping mapping;

    MatrixRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.mapping = mapping;
        this.ph = ph;
        parsePref(pref);
    }

    private void parsePref(Preferences pref) {
        includeHeader = pref.getBoolean(MatrixOptions.INCLUDE_HEADER, true);
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            StringBuilder sb;
            int cnt = 0;
            if (ph != null) {
                ph.start(mapping.getNumberOfRows());
            }
            if (includeHeader) {
                sb = new StringBuilder();
                Dataset<? extends Instance> dataset = mapping.getDataset();
                for (int j = 0; j < mapping.getNumberOfColumns(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(dataset.getAttribute(mapping.getColumnIndex(j)).getName());
                }
                sb.append("\n");
                fw.write(sb.toString());
            }
            for (int i = 0; i < mapping.getNumberOfRows(); i++) {
                sb = new StringBuilder();
                for (int j = 0; j < mapping.getNumberOfColumns(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(mapping.getMappedValue(i, j));
                }
                sb.append(",").append(mapping.getRowsResult().getInstance(i).getName());
                sb.append(",").append(mapping.getRowsResult().getInstance(i).getId());
                sb.append("\n");
                fw.write(sb.toString());
                if (ph != null) {
                    ph.progress(cnt++);
                }
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
