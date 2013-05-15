package org.clueminer.evaluation.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;

/**
 * @see http://en.wikipedia.org/wiki/Fowlkes%E2%80%93Mallows_index
 * @author Tomas Barton
 */
public class JaccardIndex extends ExternalEvaluator {

    private static final long serialVersionUID = -1547620533572167033L;
    private static String name = "Jaccard";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param clusters
     * @param dataset
     * @return Jaccard index which should be between 0.0 and 1.0 (bigger is
     * better)
     */
    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.countPairs(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double jaccard;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching, cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            jaccard = tp / (double) (tp + fp + fn);
            index += jaccard;
        }

        //average value
        return index / clusters.size();
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }
}
