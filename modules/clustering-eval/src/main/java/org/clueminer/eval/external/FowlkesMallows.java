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
import org.openide.util.lookup.ServiceProvider;

/**
 * Fowlkes-Mallows coefficient
 *
 * A Method for Comparing Two Hierarchical Clusterings - E. B. Fowlkes, C. L.
 * Mallows Journal of the American Statistical Association Vol. 78, Iss. 383,
 * 1983
 *
 * @see http://en.wikipedia.org/wiki/Fowlkes%E2%80%93Mallows_index
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class FowlkesMallows extends AbstractExternalEval {

    private static final long serialVersionUID = 101045082257039885L;
    private static final String name = "Fowlkes-Mallows";

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
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    public double countScore(Table<String, String, Integer> table) {
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double fowles;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            fowles = tp / Math.sqrt((tp + fp) * (tp + fn));
            index += fowles;
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
    public boolean isMaximized() {
        return true;
    }
}
