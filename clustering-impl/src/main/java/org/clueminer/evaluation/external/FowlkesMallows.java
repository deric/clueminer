package org.clueminer.evaluation.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public class FowlkesMallows extends ExternalEvaluator {

    private static final long serialVersionUID = 101045082257039885L;
    private static String name = "Fowlkes-Mallows";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.countPairs(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double fowles;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching, cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            fowles = tp * Math.sqrt((tp + fp) * (tp + fn));
            index += fowles;
        }

        //average value
        return index / clusters.size();
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Should be maximized
     * @param score1
     * @param score2
     * @return 
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return (score1 > score2);
    }
}
