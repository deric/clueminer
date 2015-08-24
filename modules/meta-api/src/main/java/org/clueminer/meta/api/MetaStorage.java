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
 * @param <E>
 * @param <C>
 */
public interface MetaStorage<E extends Instance, C extends Cluster<E>> {

    /**
     * Name of the storage
     *
     * @return
     */
    String getName();

    /**
     * Find score in previous results
     *
     * @param dataset
     * @param clustering
     * @param eval
     * @return
     */
    double findScore(Dataset<E> dataset, Clustering<E, C> clustering, ClusterEvaluation<E, C> eval);

    /**
     * All meta-algorithms used for generating results
     *
     * @return list of known algorithms
     */
    Collection<String> getEvolutionaryAlgorithms();

    /**
     * Creates new ID for run of an evolution on given dataset
     *
     * @param evolution contains parameters and information about dataset
     * @return ID of the run
     */
    int registerRun(Evolution evolution);

    /**
     * Stores information about dataset, clustering with all metrics available
     *
     * @param dataset
     * @param clustering
     */
    void add(Dataset<E> dataset, Clustering<E, C> clustering);

    /**
     * Associates result with given run of an evolutionary algorithm
     *
     * @param runId
     * @param clustering
     */
    void add(int runId, Clustering<E, C> clustering);

    /**
     * Find all results of given algorithm sorted by an evaluation score
     *
     * @param dataset
     * @param evolutionaryAlgorithm
     * @param score
     * @return
     */
    Collection<MetaResult> findResults(Dataset<E> dataset, String evolutionaryAlgorithm, ClusterEvaluation<E, C> score);
}
