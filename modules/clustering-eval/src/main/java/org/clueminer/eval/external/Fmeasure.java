package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.openide.util.lookup.ServiceProvider;

/**
 * F-measure or F-score
 *
 * @see http://en.wikipedia.org/wiki/F1_score
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Fmeasure extends AbstractCountingPairs {

    private static final String name = "F-measure";
    private static final long serialVersionUID = 5075558180348805172L;
    private double beta = 1.0;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double squareBeta = Math.pow(beta, 2);
        double fmeasure;
        Cluster c;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            c = ref.get(cluster);
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
                tp = res.get("tp");
                fp = res.get("fp");
                fn = res.get("fn");
                fmeasure = (1 + squareBeta) * tp / ((1.0 + squareBeta) * tp + squareBeta * fn + fp);
                index += fmeasure;
            }
        }

        //average value
        return index / table.columnKeySet().size();
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
