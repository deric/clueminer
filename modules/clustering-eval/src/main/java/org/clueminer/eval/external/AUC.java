package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.openide.util.lookup.ServiceProvider;

/**
 * Area Under Curve
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

    @Override
    public double countScore(PairMatch pm) {
        return ((pm.tp / (double) (pm.tp + pm.fn)) + (pm.tn / (double) (pm.fp + pm.tn))) / 2.0;
    }

}
