package org.clueminer.evolution.multim;

import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;

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
    public void started(Evolution evolution) {
    }

    @Override
    public void resultUpdate(Individual[] result, boolean isExplicit) {
    }

}
