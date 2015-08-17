package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class HybridCentroidSimilarity extends AbstractEvaluator {

    private static final String NAME = "Hybrid Centroid Similarity";
    private static final long serialVersionUID = 5859566115007803560L;

    public HybridCentroidSimilarity() {
        dm = EuclideanDistance.getInstance();
    }

    public HybridCentroidSimilarity(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        ClusterEvaluation ceTop = new SumOfCentroidSimilarities();// I_2
        double sum = ceTop.score(clusters, params);
        ClusterEvaluation ce = new TraceScatterMatrix();// E_1
        sum /= ce.score(clusters, params);

        return sum;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be maximized
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }
}
