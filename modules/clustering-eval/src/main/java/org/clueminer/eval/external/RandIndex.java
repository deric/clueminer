package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.clustering.api.ExternalEvaluator;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.utils.Matching;
import org.openide.util.lookup.ServiceProvider;

/**
 * Rand Index as defined in:
 *
 * L. Hubert and P. Arabie. Comparing partitions. Journal of Classification,
 * 2:193–218, 1985.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class RandIndex extends AbstractCountingPairs {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String name = "Rand Index";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Should be maximized, lies in interval <0.0 ; 1.0> where 1.0 is the best
     * value
     *
     * @param table
     * @param ref
     * @param matching
     * @return
     */
    @Override
    public double countScore(Table<String, String, Integer> table,
            Clustering<? extends Cluster> ref, Matching matching) {
        Map<String, Integer> res;

        int tp, fp, fn, tn;
        double index = 0.0;
        double rand;
        Cluster c;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            c = ref.get(cluster);
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
                tp = res.get("tp");
                fp = res.get("fp");
                fn = res.get("fn");
                tn = res.get("tn");
                rand = (tp + tn) / (double) (tp + fp + fn + tn);
                index += rand;
            }
        }

        //average value - divided by number of "real" classes
        return index / table.columnKeySet().size();
    }
}
