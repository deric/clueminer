package org.clueminer.export.sim;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.gui.ClusteringExport;
import org.clueminer.export.impl.AbstractExporter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringExport.class)
public class MatrixExporter extends AbstractExporter implements ClusteringExport {

    public static final String title = "Export similarity matrix";
    public static final String ext = ".csv";
    private MatrixOptions options;

    @Override
    public String getName() {
        return title;
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new MatrixOptions();
        }
        return options;
    }

    @Override
    public String getExtension() {
        return ext;
    }

    @Override
    public void updatePreferences(Preferences p) {
        options.updatePreferences(p);
    }

    @Override
    public FileFilter getFileFilter() {
        if (fileFilter == null) {
            fileFilter = new FileFilter() {

                @Override
                public boolean accept(File file) {
                    String filename = file.getName();
                    return file.isDirectory() || filename.endsWith(ext);
                }

                @Override
                public String getDescription() {
                    return "CSV (*.csv)";
                }
            };
        }
        return fileFilter;
    }

    @Override
    public Runnable getRunner(File file, DendroViewer analysis, Preferences pref, ProgressHandle ph) {
        return new MatrixRunner(file, analysis, pref, ph);
    }

}
