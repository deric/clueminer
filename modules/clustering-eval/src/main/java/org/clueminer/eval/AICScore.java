package org.clueminer.eval;

import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;
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

    private static final String NAME = "AIC score";
    private static final long serialVersionUID = -8805325971847590600L;
    private static final LogLikelihoodFunction likelihood = new LogLikelihoodFunction();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        // number of free parameters K
        double k = 1;
        // loglikelihood log(L)
        double l = likelihood.loglikelihoodsum(clusters);
        // AIC score
        double aic = 2 * k - 2 * l;
        return aic;
    }

    /**
     * Proximity matrix doesn't help here
     *
     * @param clusters
     * @param dataset
     * @param proximity
     * @return
     */
    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
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
}
