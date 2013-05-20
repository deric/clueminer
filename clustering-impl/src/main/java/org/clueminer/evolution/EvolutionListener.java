package org.clueminer.evolution;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionListener extends EventListener {

    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external);

    public void finalResult(Evolution evolution, int g, Individual best, 
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external);
}
