package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
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
public class FowlkesMallows extends AbstractCountingPairs {

    private static final long serialVersionUID = 101045082257039885L;
    private static final String name = "Fowlkes-Mallows";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        return pm.tp / Math.sqrt((pm.tp + pm.fp) * (pm.tp + pm.fn));
    }

}
