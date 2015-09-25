package org.clueminer.export.newick;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.gui.ClusteringExport;
import org.clueminer.export.impl.ClusteringExporter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringExport.class)
public class NewickExporter extends ClusteringExporter implements ClusteringExport {

    public static final String title = "Export to Newick";
    public static final String ext = ".nwk";
    private NewickOptions options;

    public NewickExporter() {
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new NewickOptions();
        }
        return options;
    }

    @Override
    public String getName() {
        return title;
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
                    return "Newick (*.nwk)";
                }
            };
        }
        return fileFilter;
    }

    @Override
    public String getExtension() {
        return ext;
    }

    @Override
    public Runnable getRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        return new NewickExportRunner(file, mapping, pref, ph);
    }

}
