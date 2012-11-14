package org.clueminer.clustering.api;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringListener extends EventListener {

    public void clusteringChanged(Clustering clust);
    
    public void resultUpdate(HierarchicalResult hclust);
}
