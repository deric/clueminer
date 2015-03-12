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
 */
@ServiceProvider(service = InternalEvaluator.class)
public class AICScore extends AbstractEvaluator {

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
