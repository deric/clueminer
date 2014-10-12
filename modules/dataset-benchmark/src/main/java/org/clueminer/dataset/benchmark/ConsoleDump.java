package org.clueminer.dataset.benchmark;

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
public class ConsoleDump implements EvolutionListener {

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external) {
        Clustering<Cluster> clusters = best.getClustering();
        System.out.println("============== " + generationNum);
        System.out.println("external = " + external);

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
}
