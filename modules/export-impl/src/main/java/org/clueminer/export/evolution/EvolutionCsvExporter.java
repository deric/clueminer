package org.clueminer.export.evolution;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.gui.EvolutionExport;
import org.clueminer.export.impl.AbstractExporter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = EvolutionExport.class)
public class EvolutionCsvExporter extends AbstractExporter implements EvolutionExport {

    private static final String name = "CSV";
    private CsvEvolutionOptions options;
    private Evolution evolution;
    private static final String ext = ".csv";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new CsvEvolutionOptions();
        }
        return options;
    }

    @Override
    public String getExtension() {
        return ".csv";
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
    public Runnable getRunner(File file, Evolution evolution, Preferences pref, ProgressHandle ph) {
        return new EvolutionCsvRunner(file, evolution, pref, ph);
    }

    @Override
    public void setEvolution(Evolution e) {
        this.evolution = e;
    }

    @Override
    public boolean hasData() {
        return evolution != null;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return getRunner(file, evolution, pref, ph);
    }

}
