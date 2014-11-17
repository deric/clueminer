package org.clueminer.meta.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public interface MetaStorage {

    void add(String datasetName, Clustering<? extends Cluster> clustering);

    /**
     * Find score in previous results
     *
     * @param datasetName
     * @param clustering
     * @param eval
     * @return
     */
    double findScore(String datasetName, Clustering<? extends Cluster> clustering, ClusterEvaluation eval);

}
