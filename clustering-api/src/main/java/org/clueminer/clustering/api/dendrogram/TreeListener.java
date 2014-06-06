package org.clueminer.clustering.api.dendrogram;

import java.util.EventListener;

public interface TreeListener extends EventListener {

    /**
     * Invoked when a new cluster is selected.
     *
     * @param source
     * @param cluster
     * @param data
     */
    void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data);

    /**
     * Called when tree changes its size
     *
     * @param source
     * @param width
     * @param height
     */
    void treeUpdated(DendrogramTree source, int width, int height);
}
