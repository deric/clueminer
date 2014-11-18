package org.clueminer.export.impl;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.gui.ClusteringExport;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusteringExporter extends AbstractExporter implements ClusteringExport {

    protected DendroViewer viewer;

    @Override
    public void setViewer(DendroViewer analysis) {
        this.viewer = analysis;
    }

    @Override
    public boolean hasData() {
        return viewer != null;
    }


    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return getRunner(file, viewer, pref, ph);
    }

}
