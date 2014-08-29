package org.clueminer.clustering.api.evolution;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionListener extends EventListener {

    /**
     * Best individual found in a generation
     *
     * @param generationNum
     * @param best
     * @param avgFitness
     * @param external
     */
    void bestInGeneration(int generationNum, Individual best, double avgFitness, double external);

    void finalResult(Evolution evolution, int g, Individual best,
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external);
}
