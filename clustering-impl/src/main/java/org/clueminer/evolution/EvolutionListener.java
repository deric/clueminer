package org.clueminer.evolution;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionListener extends EventListener {

    public void bestInGeneration(int generationNum, Individual best, double avgFitness);

    public void finalResult(Individual best, long evolutionTime);
}
