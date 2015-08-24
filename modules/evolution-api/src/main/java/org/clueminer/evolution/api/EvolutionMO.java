package org.clueminer.evolution.api;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public interface EvolutionMO<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends Evolution<I, E, C> {

    /**
     * Number of objectives that is evolution trying to optimize
     *
     * @return
     */
    int getNumObjectives();

    /**
     * Add objective function to evolution process
     *
     * @param objective
     */
    void addObjective(ClusterEvaluation<E, C> objective);

    /**
     * Removes given objective
     *
     * @param objective
     */
    void removeObjective(ClusterEvaluation<E, C> objective);

    /**
     * Get i-th objectives, first one is at index 0
     *
     * @param i
     * @return i-th objective
     */
    ClusterEvaluation<E, C> getObjective(int i);

    /**
     * Remove all objectives
     */
    void clearObjectives();

    /**
     * Objectives that are being used in evolution process
     *
     * @return
     */
    List<ClusterEvaluation<E, C>> getObjectives();

    /**
     * Number of solution which will be returned from evolution
     *
     * @return
     */
    int getNumSolutions();

    /**
     * Number of solution which will be returned from evolution
     *
     * @param numSolutions
     */
    void setNumSolutions(int numSolutions);

}
