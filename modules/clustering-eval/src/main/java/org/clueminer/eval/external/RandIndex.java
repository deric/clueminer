package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.openide.util.lookup.ServiceProvider;

/**
 * Rand Index as defined in:
 *
 * L. Hubert and P. Arabie. Comparing partitions. Journal of Classification,
 * 2:193–218, 1985.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class RandIndex extends AbstractCountingPairs {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String name = "Rand Index";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Should be maximized, lies in interval <0.0 ; 1.0> where 1.0 is the best
     * value
     *
     * In literature usually referred with letters
     * tp = a, fp = b, fn = c, tn = d
     *
     * @param pm
     * @return
     */
    @Override
    public double countScore(PairMatch pm) {
        return (pm.tp + pm.tn) / (double) (pm.tp + pm.fp + pm.fn + pm.tn);
    }

}
