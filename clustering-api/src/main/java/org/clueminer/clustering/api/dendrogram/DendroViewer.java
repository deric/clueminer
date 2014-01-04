package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;

/**
 * Interface for a dendrogram displayer
 *
 * @author Tomas Barton
 */
public interface DendroViewer {

    /**
     * One heatmap size should be same as dendrogram branches spacing
     *
     * @return size of one heatmap cell
     */
    public Dimension getElementSize();

    public void addDendrogramDataListener(DendrogramDataListener listener);

    public void removeDendrogramDataListener(DendrogramDataListener listener);

    public void addClusteringListener(ClusteringListener listener);

    /**
     * Fire an event when user modifies clustering (changing cutoff)
     *
     * @param clust
     */
    public void fireClusteringChanged(Clustering clust);

}
