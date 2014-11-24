package org.clueminer.clustering.api.evolution;

import java.util.Collection;
import java.util.EventListener;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionListener extends EventListener {

    void resultUpdate(Collection<Clustering<? extends Cluster>> result);

    /**
     * Best individual found in a generation
     *
     * @param generationNum
     * @param external
     * @param population
     */
    void bestInGeneration(int generationNum, Population<? extends Individual> population, double external);

    void finalResult(Evolution evolution, int g, Individual best,
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external);
}
