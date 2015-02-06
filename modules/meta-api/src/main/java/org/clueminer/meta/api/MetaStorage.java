package org.clueminer.meta.api;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;

/**
 *
 * @author Tomas Barton
 */
public interface MetaStorage {

    /**
     * Name of the storage
     *
     * @return
     */
    String getName();

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

    /**
     * All meta-algorithms used for generating results
     *
     * @return list of known algorithms
     */
    Collection<? extends Evolution> getEvolutionaryAlgorithms();

    /**
     * Creates new ID for run of an evolution on given dataset
     *
     * @param evolution
     * @param dataset
     * @return ID of the run
     */
    int registerRun(Evolution evolution, Dataset<? extends Instance> dataset);

}
