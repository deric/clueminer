package org.clueminer.eval.external;

import org.clueminer.eval.utils.Matching;
import com.google.common.collect.Table;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractCountingPairs extends AbstractExternalEval {

    private static final long serialVersionUID = -8708340302697665494L;

    public abstract double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref, Matching matching);

    /**
     * Once matching classes <-> clusters are found result will be stored in
     * clustering lookup
     *
     * @param table
     * @param ref
     * @return
     */
    public double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref) {
        Matching matching = ref.getLookup().lookup(Matching.class);
        //we don't expect mapping to original to change, so we can store the result
        if (matching == null) {
            matching = CountingPairs.findMatching(table);
            ref.lookupAdd(matching);
        }
        return countScore(table, ref, matching);
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2, Props params) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(c1, c2);
        //don't store mapping when comparing list of clusterings (too many posibilities)
        return countScore(table, c1, CountingPairs.findMatching(table));
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        return countScore(table, clusters);
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering clusters) {
        return score(clusters, new Props());
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

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
