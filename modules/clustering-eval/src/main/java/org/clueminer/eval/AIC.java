package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Distance function is not used in this score.
 *
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 *
 * @see
 * AKAIKE, H. A new look at the statistical model identification. IEEE
 * Transactions on Automatic Control 19, 6 (December 1974), 716–723.
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class AIC extends AbstractEvaluator {

    private static final String NAME = "AIC";
    private static final long serialVersionUID = -8805325971847590600L;
    private static final LogLikelihoodFunction likelihood = new LogLikelihoodFunction();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        // number of free parameters K
        double k = 1;
        likelihood.setAlpha0(params.getDouble("likelihood.alpha", 0.1));
        likelihood.setBeta0(params.getDouble("likelihood.beta", 0.1));
        likelihood.setLambda0(params.getDouble("likelihood.lambda", 0.1));
        likelihood.setMu0(params.getDouble("likelihood.mu", 0.0));
        // loglikelihood log(L)
        double l = likelihood.loglikelihoodsum(clusters);
        // AIC score
        double aic = 2 * k - 2 * l;
        return aic;
    }

    /**
     * Compares the two scores AIC scores. Returns true if the first score is
     * 'better' than the second score.
     *
     *
     * don't use abs values
     *
     * @link http://stats.stackexchange.com/questions/84076/negative-values-for-aic-in-general-mixed-model
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimalized
        return compare(score1, score2) < 0;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.NEGATIVE_INFINITY;
    }

}
