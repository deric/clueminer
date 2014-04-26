package org.clueminer.eval;

import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.clustering.api.ClusterEvaluator;
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
@ServiceProvider(service = ClusterEvaluator.class)
public class AICScore extends ClusterEvaluator {

    private static final String NAME = "AIC score";
    private static final long serialVersionUID = -8805325971847590600L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        // number of free parameters K
        double k = 1;
        LogLikelihoodFunction likelihood = new LogLikelihoodFunction();
        // loglikelihood log(L)
        double l = likelihood.loglikelihoodsum(clusters);
        // AIC score
        double aic = -2 * l + 2 * k;
        return aic;
    }

    /**
     * Proximity matrix doesn't help here
     *
     * @param clusters
     * @param proximity
     * @return
     */
    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be minimalized
        return Math.abs(score1) < Math.abs(score2);
    }
}
