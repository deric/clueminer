package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.evolution.Evolution;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Interface for exporting evolution results
 *
 * @author Tomas Barton
 */
public interface EvolutionExport extends ExporterGUI {

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
     * Evolution is used for obtaining data
     *
     * @param evolution
     */
    void setEvolution(Evolution evolution);
}
