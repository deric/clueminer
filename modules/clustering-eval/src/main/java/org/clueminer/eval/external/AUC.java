package org.clueminer.eval.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.CountingPairs;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class AUC extends AbstractCountingPairs {

    private static final long serialVersionUID = -7408696944404937976L;
    private static final String name = "AUC";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param table
     * @param ref
     * @return
     */
    @Override
    public double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fn, fp, tn;
        double auc = 0.0;
        Cluster c;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            c = ref.get(cluster);
            //clusters with size 1 should not increase accuracy
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
                tp = res.get("tp");
                fp = res.get("fp");
                tn = res.get("tn");
                fn = res.get("fn");
                auc += ((tp / (double) (tp + fn)) + (tn / (double) (fp + tn))) / 2.0;
            }
        }

        //average value - divided by known number of classes (or should we divide it by number of clusters?)
        return auc / table.columnKeySet().size();
    }
}
