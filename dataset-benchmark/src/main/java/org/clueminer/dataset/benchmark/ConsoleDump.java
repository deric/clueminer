package org.clueminer.dataset.benchmark;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evaluation.external.CountingPairs;
import org.clueminer.evolution.Evolution;
import org.clueminer.evolution.EvolutionListener;
import org.clueminer.evolution.Individual;
import org.clueminer.evolution.Pair;

/**
 *
 * @author Tomas Barton
 */
public class ConsoleDump implements EvolutionListener {
    
    
    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external) {
        Clustering<Cluster> clusters = best.getClustering();
        Table<String, String, Integer> table = CountingPairs.countPairs(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        System.out.println("============== " + generationNum);
        System.out.println("table: " + table);
        System.out.println("matching: " + matching);
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
}
