package org.clueminer.evolution.bnb;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;

/**
 *
 * @author Tomas Barton
 */
public class ConsoleReporter implements EvolutionListener {

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external) {
        System.out.println("gen: " + generationNum + ", avg: " + avgFitness + ", exter: " + external);
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        System.out.println("======================================");
        System.out.println("===== evolution finished =============");
        System.out.println("evolution: " + evolution.getName());
        System.out.println("generations: " + g);
        System.out.println("exter: " + external);
        System.out.println("======================================");
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {
    }

}
