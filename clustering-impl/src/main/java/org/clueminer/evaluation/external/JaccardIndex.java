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
 * @see http://en.wikipedia.org/wiki/Jaccard_index
 * @author Tomas Barton
 */
public class JaccardIndex extends ExternalEvaluator {

    private static final long serialVersionUID = -1547620533572167033L;
    private static String name = "Jaccard";

    @Override
    public String getName() {
        return name;
    }

    private double countScore(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double jaccard;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            jaccard = tp / (double) (tp + fp + fn);
            index += jaccard;
        }

        //average value
        return index / table.columnKeySet().size();
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
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

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
