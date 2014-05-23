package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
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
public class Precision extends ExternalEvaluator {

    private static final long serialVersionUID = -1547620533572167033L;
    private static final String name = "Precision";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table);
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    public double countScore(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;
        int tp, fp;
        double index = 0.0;
        double precision;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            //System.out.println("class: " + matching.inverse().get(cluster) + " cluster = " + cluster);

            //System.out.println(res);
            tp = res.get("tp");
            fp = res.get("fp");
            //System.out.println("sum = " + (tp + fp + res.get("fn") + res.get("tn")));
            precision = tp / (double) (tp + fp);
            //System.out.println("precision = " +precision);
            index += precision;
        }

        //average value
        return index / table.columnKeySet().size();
    }

    /**
     * Bigger is better
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }
}
