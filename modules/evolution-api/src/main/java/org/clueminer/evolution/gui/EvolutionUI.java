package org.clueminer.evolution.gui;

import org.clueminer.evolution.api.Evolution;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionUI {

    /**
     * Identifier of this UI
     *
     * @return
     */
    String getName();

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

    /**
     * Test whether given algorithm is supported by the UI
     *
     * @param evolve
     * @return
     */
    boolean isUIfor(Evolution evolve);
}
