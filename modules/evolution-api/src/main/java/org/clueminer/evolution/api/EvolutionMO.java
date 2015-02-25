package org.clueminer.evolution.api;

import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface EvolutionMO<T extends Individual> extends Evolution<T> {

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
    void addObjective(ClusterEvaluation objective);

    /**
     * Removes given objective
     *
     * @param objective
     */
    void removeObjective(ClusterEvaluation objective);

    /**
     * Remove all objectives
     */
    void clearObjectives();

    /**
     * Objectives that are being used in evolution process
     *
     * @return
     */
    List<ClusterEvaluation> getObjectives();

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
