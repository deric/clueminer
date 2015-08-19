package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractCountingPairs<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> {

    private static final long serialVersionUID = -8708340302697665494L;

    public abstract double countScore(PairMatch pm);

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering<E, C> clusters) {
        return score(clusters, new Props());
    }

    /**
     * Once matching classes <-> clusters are found result will be stored in
     * clustering lookup
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        PairMatch pm = clusters.getLookup().lookup(PairMatch.class);
        //we don't expect mapping to original to change, so we can store the result
        if (pm == null) {
            pm = CountingPairs.getInstance().matchPairs(clusters);
            clusters.lookupAdd(pm);
        }
        return countScore(pm);
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        PairMatch pm = CountingPairs.getInstance().matchPairs(c1, c2);
        return countScore(pm);
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
