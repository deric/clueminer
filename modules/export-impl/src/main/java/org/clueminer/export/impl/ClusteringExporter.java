package org.clueminer.export.impl;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.gui.ClusteringExport;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusteringExporter extends AbstractExporter implements ClusteringExport {

    protected DendroViewer viewer;
    protected DendrogramMapping mapping;
    protected Clustering clustering;

    @Override
    public void setViewer(DendroViewer analysis) {
        this.viewer = analysis;
        this.mapping = analysis.getDendrogramMapping();
        this.clustering = analysis.getDendrogramMapping().getColumnsClustering();
    }

    @Override
    public void setDendrogramMapping(DendrogramMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void setClustering(Clustering clustering) {
        this.clustering = clustering;
    }

    @Override
    public boolean hasData() {
        return viewer != null;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return getRunner(file, mapping, pref, ph);
    }

}
