package org.clueminer.evaluation.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;

/**
 * Rand Index as defined in:
 *
 * L. Hubert and P. Arabie. Comparing partitions. Journal of Classification,
 * 2:193–218, 1985.
 *
 * @author Tomas Barton
 */
public class RandIndex extends ExternalEvaluator {

    private static final long serialVersionUID = -7408696944704938976L;
    private static String name = "Rand Index";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn, tn;
        double index = 0.0;
        double rand;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            tn = res.get("tn");
            rand = (tp + tn) / (double) (tp + fp + fn + tn);
            index += rand;
        }

        //average value
        return index / clusters.size();
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Should be maximized, lies in interval <0.0 ; 1.0> where 1.0 is the best
     * value
     *
     * @param score1
     * @param score2
     * @return true if score1 is better than score2
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
