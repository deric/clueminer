package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
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
     * @param mapping
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, DendrogramMapping mapping, Preferences pref, final ProgressHandle ph);

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

    /**
     * Dendrogram mapping
     *
     * @param mapping
     */
    void setDendrogramMapping(DendrogramMapping mapping);

    /**
     *
     * @param clustering
     */
    void setClustering(Clustering clustering);
}
