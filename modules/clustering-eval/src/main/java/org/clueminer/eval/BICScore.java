package org.clueminer.eval;

import org.clueminer.eval.utils.LogLikelihoodFunction;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ClusterEvaluator.class)
public class BICScore extends ClusterEvaluator {

    private static final String NAME = "BIC score";
    private static final long serialVersionUID = -8771446315217152042L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
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
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be minimzed.
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

}
