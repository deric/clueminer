package org.clueminer.export.impl;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class CsvExporter extends AbstractExporter implements ActionListener, PropertyChangeListener {

    private static CsvExporter instance;
    private CsvOptions options;
    public static final String title = "Export to CSV";
    public static final String ext = ".csv";

    public static CsvExporter getDefault() {
        if (instance == null) {
            instance = new CsvExporter();
        }
        return instance;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new CsvOptions();
        }
        return options;
    }

    private CsvExporter() {
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
    public Runnable getRunner(File file, ClusterAnalysis analysis, Preferences pref, ProgressHandle ph) {
        return new CsvExportRunner(file, analysis, pref, ph);
    }

}
