package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * I_3 from the Zhao 2001 paper
 *
 * TODO uitleg
 *
 * @author Andreas De Rijcke
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class SumOfSquaredErrors extends ClusterEvaluator {

    private static String NAME = "Sum of squared errors";
    private static final long serialVersionUID = 7246192305561714193L;

    public SumOfSquaredErrors() {
        dm = EuclideanDistance.getInstance();
    }

    public SumOfSquaredErrors(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double sum = 0;
        Dataset clust;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            double tmpSum = 0;
            for (int j = 0; j < clust.size(); j++) {
                for (int k = 0; k < clust.size(); k++) {
                    double error = dm.measure(clust.instance(j), clust.instance(k));
                    tmpSum += error * error;
                }

            }
            sum += tmpSum / clust.size();
        }
        return sum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // TODO solve bug: score is NaN when clusters with 0 instances
        // should be minimized
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

}
