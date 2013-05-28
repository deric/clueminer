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
 *
 * @author Tomas Barton
 */
public class Recall extends ExternalEvaluator {

    private static final long serialVersionUID = -1547620533572167033L;
    private static String name = "Recall";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Count Recall against class labels
     *
     * @param clusters
     * @param dataset
     * @return
     */
    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    public double countScore(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fn;
        double index = 0.0;
        double precision;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            tp = res.get("tp");
            fn = res.get("fn");
            precision = tp / (double) (tp + fn);
            index += precision;
        }

        //average value
        return index / matching.size();
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table);
    }
}
