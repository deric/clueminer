package org.clueminer.eval.external;

import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.clustering.api.ExternalEvaluator;
import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
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
            Clustering<? extends Cluster> ref, BiMap<String, String> matching) {
        Map<String, Integer> res;

        int tp, fn;
        double index = 0.0;
        double precision;
        Cluster c;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            c = ref.get(cluster);
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
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
