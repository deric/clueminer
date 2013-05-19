package org.clueminer.evolution;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evaluation.external.CountingPairs;
import org.clueminer.evaluation.external.ExternalEvaluator;

/**
 *
 * @author Tomas Barton
 */
public class ConsoleDump implements EvolutionListener {
    
    private ExternalEvaluator external;
    
    public ConsoleDump(ExternalEvaluator external) {
        this.external = external;
    }
    
    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness) {
        Clustering<Cluster> clusters = best.getClustering();
        Table<String, String, Integer> table = CountingPairs.countPairs(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;
        System.out.println("============== " + generationNum);
        System.out.println("table: " + table);
        System.out.println("matching: " + matching);
        int tp, fp, fn;
        double index = 0.0;
        double jaccard;
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching, cluster);
            System.out.println(cluster + ": " + res);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            jaccard = tp / (double) (tp + fp + fn);
            System.out.println("jc = " + jaccard);
            index += jaccard;
        }
        System.out.println("jaccard = " + (index / clusters.size()));
        System.out.println("external = " + external.score(clusters, null));
        
    }
    
    @Override
    public void finalResult(int generations, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness) {
        long evoTime = (long) ((time.b - time.a) / 1000.0);
        System.out.println("Evolution has finished after " + evoTime + " s...");
        System.out.println("avgFit(G:0)= " + avgFitness.a + " avgFit(G:" + (generations - 1) + ")= " + avgFitness.b + " -> " + ((avgFitness.b / avgFitness.a) * 100) + " %");
        System.out.println("bstFit(G:0)= " + bestFitness.a + " bstFit(G:" + (generations - 1) + ")= " + bestFitness.b + " -> " + ((bestFitness.b / bestFitness.a) * 100) + " %");
        System.out.println("bestIndividual= " + best);
    }
}
