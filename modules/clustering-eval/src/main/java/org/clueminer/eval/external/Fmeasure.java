package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.openide.util.lookup.ServiceProvider;

/**
 * F-measure or F-score
 *
 * @see http://en.wikipedia.org/wiki/F1_score
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Fmeasure extends AbstractCountingPairs {

    private static final String name = "F-measure";
    private static final long serialVersionUID = 5075558180348805172L;
    private double beta = 1.0;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        double squareBeta = Math.pow(beta, 2);
        return (1 + squareBeta) * pm.tp / ((1.0 + squareBeta) * pm.tp + squareBeta * pm.fn + pm.fp);
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
