package org.clueminer.export.impl;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.gui.ClusteringExportGui;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusteringExportGui.class)
public class CsvExporter extends ClusteringExporter implements ActionListener, ClusteringExportGui {

    private CsvOptions options;
    public static final String title = "Export to CSV";
    public static final String EXT = ".csv";

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
        return EXT;
    }

    @Override
    public Runnable getRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        return new CsvExportRunner(file, mapping, pref, ph);
    }

    @Override
    public boolean hasData() {
        return mapping != null || clustering != null;
    }

}
