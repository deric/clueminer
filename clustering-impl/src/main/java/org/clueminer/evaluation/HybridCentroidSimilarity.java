package org.clueminer.evaluation;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class HybridCentroidSimilarity extends ClusterEvaluator {

    private static String NAME = "Hybrid Centroid Similarity";
    private static final long serialVersionUID = 5859566115007803560L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        ClusterEvaluation ceTop = new SumOfCentroidSimilarities();// I_2
        double sum = ceTop.score(clusters, dataset);
        ClusterEvaluation ce = new TraceScatterMatrix();// E_1
        sum /= ce.score(clusters, dataset);

        return sum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be maximized
        return score1 > score2;
    }
}