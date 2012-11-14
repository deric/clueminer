package org.clueminer.clustering.api.dendrogram;

import java.util.EventListener;

public interface TreeListener extends EventListener {

    /**
     * Invoked when a new cluster is selected.
     */
    void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data);
    
    void treeUpdated(DendrogramTree source, int width, int height);
}
