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
 * @see http://en.wikipedia.org/wiki/Jaccard_index
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class JaccardIndex extends AbstractCountingPairs {

    private static final long serialVersionUID = -1547620533572167033L;
    private static final String name = "Jaccard";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param table
     * @param ref
     * @param matching
     * @return Jaccard index which should be between 0.0 and 1.0 (bigger is
     * better)
     */
    @Override
    public double countScore(Table<String, String, Integer> table,
            Clustering<? extends Cluster> ref, Matching matching) {
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double jaccard;
        Cluster c;
        //for each cluster we have score of quality
        for (Map.Entry<String, String> entry : matching.entrySet()) {
            c = ref.get(entry.getValue());
            //clusters with size 1 should not increase accuracy
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.get(entry.getKey()), entry.getValue());
                tp = res.get("tp");
                fp = res.get("fp");
                fn = res.get("fn");
                jaccard = tp / (double) (tp + fp + fn);
                index += jaccard;
            }
        }

        //average value
        return index / table.columnKeySet().size();
    }

}
