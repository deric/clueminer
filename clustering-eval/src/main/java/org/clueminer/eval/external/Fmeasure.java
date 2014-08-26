package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * F-measure or F-score
 *
 * @see http://en.wikipedia.org/wiki/F1_score
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Fmeasure extends AbstractExternalEval {

    private static final String name = "F-measure";
    private static final long serialVersionUID = 5075558180348805172L;
    private double beta = 1.0;

    @Override
    public String getName() {
        return name;
    }

    public double countScore(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double squareBeta = Math.pow(beta, 2);
        double fmeasure;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);

            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            fmeasure = (1 + squareBeta) * tp / ((1.0 + squareBeta) * tp + squareBeta * fn + fp);
            index += fmeasure;
        }

        //average value
        return index / table.columnKeySet().size();
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        return countScore(table);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return true if score1 is better than score2
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
