package org.clueminer.evolution.multim;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Population;

/**
 *
 * @author Tomas Barton
 */
public class ConsoleReporter implements EvolutionListener {

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        System.out.println("gen: " + generationNum + ", avg: " + population.getAvgFitness() + ", exter: " + external);
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        System.out.println("======================================");
        System.out.println("===== evolution finished =============");
        System.out.println("evolution: " + evolution.getName());
        System.out.println("generations: " + g);
        System.out.println("external: " + external);
        System.out.println("internal: " + best.getFitness());
        System.out.println("num clusters: " + best.getClustering().size());
        System.out.println("best: " + best.getProps().toString());
        System.out.println("======================================");
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {
    }

}
