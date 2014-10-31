package org.clueminer.export.impl;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.gui.ClusteringExport;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringExport.class)
public class CsvExporter extends AbstractExporter implements ActionListener, ClusteringExport {

    private CsvOptions options;
    public static final String title = "Export to CSV";
    public static final String ext = ".csv";

    public CsvExporter() {
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new CsvOptions();
        }
        return options;
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
                    return file.isDirectory() || filename.endsWith(".csv");
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
    public String getExtension() {
        return ext;
    }

    @Override
    public Runnable getRunner(File file, DendroViewer analysis, Preferences pref, ProgressHandle ph) {
        return new CsvExportRunner(file, analysis, pref, ph);
    }

}
