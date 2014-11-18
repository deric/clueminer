package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringExport extends ExporterGUI {


    /**
     * Creates Runnable object for performing the export
     *
     * @param file
     * @param analysis
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, DendroViewer analysis, Preferences pref, final ProgressHandle ph);

    /**
     * Display exporting options
     */
    void showDialog();

    /**
     * Set graphical component displaying clustering
     *
     * @param analysis
     */
    void setViewer(DendroViewer analysis);
}
