package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.Matching;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Recall extends AbstractCountingPairs {

    private static final long serialVersionUID = -1547620533572167033L;
    private static final String name = "Recall";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(Table<String, String, Integer> table,
            Clustering<? extends Cluster> ref, Matching matching) {
        Map<String, Integer> res;

        int tp, fn;
        double index = 0.0;
        double precision;
        Cluster c;
        //for each cluster we have score of quality
        for (Map.Entry<String, String> entry : matching.entrySet()) {
            c = ref.get(entry.getValue());
            //clusters with size 1 should not increase accuracy
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, entry.getKey(), entry.getValue());
                tp = res.get("tp");
                fn = res.get("fn");
                precision = tp / (double) (tp + fn);
                index += precision;
            }
        }

        //average value
        return index / table.columnKeySet().size();
    }
}
