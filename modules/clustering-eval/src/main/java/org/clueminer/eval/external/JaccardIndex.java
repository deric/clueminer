package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.openide.util.lookup.ServiceProvider;

/**
 * @see http://en.wikipedia.org/wiki/Jaccard_index
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class JaccardIndex extends AbstractCountingPairs {

    private static final long serialVersionUID = -1547620533572167033L;
    private static final String name = "Jaccard";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        return pm.tp / (double) (pm.tp + pm.fp + pm.fn);
    }

}
