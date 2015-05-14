package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public interface PartitioningClustering extends ClusteringAlgorithm {

    /**
     * This method will partition the clustering algorithm on a particular data
     * set. The result will be a Clustering (a set of Datasets) where each data
     * set is a cluster
     *
     * @param dataset
     * @param params  a set of parameters (some might be required, depends on
     *                specific algorithm)
     * @return
     */
    public Clustering<? extends Cluster> partition(Dataset<? extends Instance> dataset, Props params);

}
