package org.clueminer.clustering.api.dendrogram;

import java.util.EventListener;
import org.clueminer.clustering.api.HierarchicalResult;

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

    /**
     * Triggered when tree order optimization finishes
     *
     * @param source
     * @param mapping
     */
    void leafOrderUpdated(Object source, HierarchicalResult mapping);
}
