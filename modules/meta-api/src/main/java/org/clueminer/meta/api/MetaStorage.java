package org.clueminer.meta.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface MetaStorage {

    void add(Dataset<? extends Instance> dataset, Clustering<? extends Cluster> clustering);

    /**
     * Find score in previous results
     *
     * @param dataset
     * @param clustering
     * @param eval
     * @return
     */
    double findScore(Dataset<? extends Instance> dataset, Clustering<? extends Cluster> clustering, ClusterEvaluation eval);

}
