package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.openide.util.lookup.ServiceProvider;

/**
 * Standard precision definition, in case of clustering we use definition based
 * on correctly assigned pairs of data (either comparing two clustering - one is
 * reference, or computed against class labels - same as in case of supervised
 * learning)
 *
 * @see https://en.wikipedia.org/wiki/Precision_and_recall
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Precision extends AbstractCountingPairs {

    private static final long serialVersionUID = -1547620533572167034L;
    private static final String name = "Precision";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        return pm.tp / (double) (pm.tp + pm.fp);
    }

}
