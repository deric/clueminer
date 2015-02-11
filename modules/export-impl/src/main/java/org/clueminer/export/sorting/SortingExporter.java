package org.clueminer.export.sorting;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.export.impl.AbstractExporter;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class SortingExporter extends AbstractExporter {

    public static final String title = "Export to CSV";
    public static final String ext = ".csv";
    private SortingOptions options;
    private Object2DoubleOpenHashMap<String> results;
    private Dataset<? extends Instance> dataset;

    public SortingExporter() {
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new SortingOptions();
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
    public boolean hasData() {
        return results != null;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return new SortingRunner(file, dataset, results, pref, ph);
    }


}
