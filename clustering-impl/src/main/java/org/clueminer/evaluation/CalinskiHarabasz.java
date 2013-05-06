package org.clueminer.evaluation;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.math.Matrix;

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
 * @author Tomas Barton
 */
public class CalinskiHarabasz extends ClusterEvaluator {

    private static final long serialVersionUID = -2699019526373205522L;
    private static String name = "Calinski-Harabasz";

    public CalinskiHarabasz() {
        dm = new EuclideanDistance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        if (clusters.size() > 1) {
            double w = 0.0, b = 0.0, ch;
            //centroid of all data
            Instance centroid = clusters.getCentroid();
            for (int i = 0; i < clusters.size(); i++) {
                Cluster<Instance> x = clusters.get(i);

                w += getSumOfSquaredError(x);
                b += (x.size()) * Math.pow(dm.measure(centroid, x.getCentroid()), 2);
            }
            ch = (b / (clusters.size() - 1)) / (w / (clusters.instancesCount() - clusters.size()));

            return ch;
        } else {
            /*
             * To avoid division by zero,
             * with just one cluster we can't compute index
             */
            return Double.NaN;
        }
    }

    public double getSumOfSquaredError(Cluster<Instance> x) {
        double squaredErrorSum = 0, dist;
        for (Instance inst : x) {
            dist = dm.measure(inst, x.getCentroid());
            squaredErrorSum += Math.pow(dist, 2);
        }

        return squaredErrorSum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
