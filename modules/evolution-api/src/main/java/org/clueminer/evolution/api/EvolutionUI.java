package org.clueminer.explorer;

import org.clueminer.evolution.api.Evolution;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionUI {

    /**
     * Update algorithm settings
     *
     * @param alg
     */
    void updateAlgorithm(Evolution alg);

    /**
     * number of generations
     *
     * @return
     */
    int getGenerations();

    /**
     * Population size
     *
     * @return
     */
    int getPopulation();
}
