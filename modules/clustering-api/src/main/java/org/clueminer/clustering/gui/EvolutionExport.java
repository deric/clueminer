package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.evolution.Evolution;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Interface for exporting evolution results
 *
 * @author Tomas Barton
 */
public interface EvolutionExport {

    /**
     * Name of exported format
     *
     * @return
     */
    String getName();

    /**
     *
     * @return panel with format specific options
     */
    JPanel getOptions();

    /**
     * Extension of exported format
     *
     * @return
     */
    String getExtension();

    /**
     * Method is called after user confirms dialog with format specific settings
     *
     * @param p
     */
    void updatePreferences(Preferences p);

    /**
     *
     * @return filter for specific format(s)
     */
    FileFilter getFileFilter();

    /**
     * Creates Runnable object for performing the export
     *
     * @param file
     * @param evolution
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, Evolution evolution, Preferences pref, final ProgressHandle ph);

    /**
     * Perform export
     */
    void export();
}
