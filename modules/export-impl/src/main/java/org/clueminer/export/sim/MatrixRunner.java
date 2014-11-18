package org.clueminer.export.sim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
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

    private File file;
    private DendroViewer analysis;
    private ProgressHandle ph;
    private boolean includeHeader;

    MatrixRunner(File file, DendroViewer analysis, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.analysis = analysis;
        this.ph = ph;
        parsePref(pref);
    }

    private void parsePref(Preferences pref) {
        includeHeader = pref.getBoolean(MatrixOptions.INCLUDE_HEADER, true);
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            DendrogramMapping map = analysis.getDendrogramMapping();
            StringBuilder sb;
            int cnt = 0;
            if (ph != null) {
                ph.start(map.getNumberOfRows());
            }
            if (includeHeader) {
                sb = new StringBuilder();
                Dataset<? extends Instance> dataset = map.getDataset();
                for (int j = 0; j < map.getNumberOfColumns(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(dataset.getAttribute(map.getColumnIndex(j)).getName());
                }
                sb.append("\n");
                fw.write(sb.toString());
            }
            for (int i = 0; i < map.getNumberOfRows(); i++) {
                sb = new StringBuilder();
                for (int j = 0; j < map.getNumberOfColumns(); j++) {
                    if (j > 0) {
                        sb.append(",");
                    }
                    sb.append(map.getMappedValue(i, j));
                }
                sb.append(",").append(map.getRowsResult().getInstance(i).getName());
                sb.append(",").append(map.getRowsResult().getInstance(i).getId());
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
