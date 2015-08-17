package org.clueminer.eval;

import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * The idea behind the CH measure is to compute the sum of squared errors
 * (distances) between the k-th cluster and the other k - 1 clusters, and
 * compare that to the internal sum of squared errors for the k clusters (taking
 * their individual squared error terms and summing them). In effect, this is a
 * measure of inter-cluster dissimilarity over intra-cluster dissimilarity.
 *
 * Now, if B(k) and W(k) are both measures of difference/dissimilarity, then a
 * larger CH value indicates a better clustering, since the between cluster
 * difference should be high, and the within cluster difference should be low. *
 * another way to calculate B(k) is B = T - W where T is the sum of squared
 * error for all elements to the global average
 *
 * @cite T. Calinski and J. Harabasz. A dendrite method for cluster analysis.
 * Communications in Statistics, 3, no. 1:1–27, 1974.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class CalinskiHarabasz extends AbstractEvaluator {

    private static final long serialVersionUID = -2699019526373205522L;
    private static final String name = "Calinski-Harabasz";

    public CalinskiHarabasz() {
        dm = EuclideanDistance.getInstance();
    }

    public CalinskiHarabasz(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        if (clusters.size() > 1) {
            double w = 0.0, b = 0.0;
            //centroid of all data
            Instance centroid = clusters.getCentroid();
            double d;
            for (int i = 0; i < clusters.size(); i++) {
                Cluster<? extends Instance> x = clusters.get(i);
                w += sumOfSquaredError(x);
                d = dm.measure(centroid, x.getCentroid());
                b += (x.size()) * FastMath.pow(d, 2);
            }
            double denom = w / (clusters.instancesCount() - clusters.size());
            if (denom == 0.0) {
                return 0.0;
            }
            return (b / (clusters.size() - 1)) / denom;
        } else {
            /*
             * To avoid division by zero,
             * with just one cluster we can't compute index
             */
            return Double.NaN;
        }
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return true in case that score1 is better than score2
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
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
