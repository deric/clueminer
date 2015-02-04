package org.clueminer.dataset.benchmark;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;


/**
 *
 * @author Tomas Barton
 */
public class ConsoleDump implements EvolutionListener {

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        Clustering<Cluster> clusters = population.getBestIndividual().getClustering();
        System.out.println("============== generation: " + generationNum);
        System.out.println("external = " + external);
        System.out.println("avgFit = " + population.getAvgFitness());
        System.out.println("clustering: " + clusters.toString());
        System.out.println("==============");
    }

    @Override
    public void finalResult(Evolution evol, int generations, Individual best,
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        long evoTime = (long) ((time.b - time.a) / 1000.0);
        System.out.println("Evolution has finished after " + evoTime + " s...");
        System.out.println("avgFit(G:0)= " + avgFitness.a + " avgFit(G:" + (generations - 1) + ")= " + avgFitness.b + " -> " + ((avgFitness.b / avgFitness.a) * 100) + " %");
        System.out.println("bstFit(G:0)= " + bestFitness.a + " bstFit(G:" + (generations - 1) + ")= " + bestFitness.b + " -> " + ((bestFitness.b / bestFitness.a) * 100) + " %");
        System.out.println("bestIndividual= " + best);
        System.out.println("external criterion = " + external);
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {
        //not much to do
    }

    @Override
    public void started(Evolution evolution) {
    }
}
