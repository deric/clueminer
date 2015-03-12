package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = InternalEvaluator.class)
public class BICScore extends AbstractEvaluator {

    private static final String NAME = "BIC";
    private static final long serialVersionUID = -8771446315217152042L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        // number of free parameters K
        double k = 1;
        // sampelsize N
        double datasize = 0;

        for (int i = 0; i < clusters.size(); i++) {
            datasize += clusters.get(i).size();
        }
        LogLikelihoodFunction likelihood = new LogLikelihoodFunction();
        // loglikelihood log(L)
        double l = likelihood.loglikelihoodsum(clusters);
        // BIC score
        double bic = -2 * l + Math.log10(datasize) * k;
        return bic;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimzed.
        return score1 < score2;
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
