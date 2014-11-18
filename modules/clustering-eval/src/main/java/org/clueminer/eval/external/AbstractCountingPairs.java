package org.clueminer.eval.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractCountingPairs extends AbstractExternalEval {

    public abstract double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref, BiMap<String, String> matching);

    /**
     * Once matching classes <-> clusters are found result will be stored in
     * clustering lookup
     *
     * @param table
     * @param ref
     * @return
     */
    public double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref) {
        BiMap<String, String> matching = ref.getLookup().lookup(BiMap.class);
        //we don't expect mapping to original to change, so we can store the result
        if (matching == null) {
            matching = CountingPairs.findMatching(table);
            ref.lookupAdd(matching);
        }
        return countScore(table, ref, matching);
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        //don't store mapping when comparing list of clusterings (too many posibilities)
        return countScore(table, c1, CountingPairs.findMatching(table));
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table, clusters);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Bigger is better
     *
     * @return
     */
    @Override
    public boolean isMaximized() {
        return true;
    }
}
