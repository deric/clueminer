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
 *
 * @see http://en.wikipedia.org/wiki/Accuracy_and_precision for definition
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Accuracy extends AbstractCountingPairs {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String name = "Accuracy";

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
        double index = 0.0;
        double accuracy;
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
                accuracy = (tp + tn) / (double) (tp + fn + fp + tn);
                index += accuracy;
            }
        }

        //average value - divided by known number of classes (or should we divide it by number of clusters?)
        return index / table.columnKeySet().size();
    }
}
