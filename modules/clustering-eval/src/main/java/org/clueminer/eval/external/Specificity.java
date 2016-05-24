package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Specificity extends AbstractCountingPairs {

    private static final long serialVersionUID = -1547620533572167043L;
    private static final String name = "Specificity";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        return pm.tn / (double) (pm.tn + pm.fp);
    }

}
