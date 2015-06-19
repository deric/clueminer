package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Silhouette score
 *
 * @link http://en.wikipedia.org/wiki/Silhouette_(clustering)
 * @see  Peter J. Rousseeuw (1987). "Silhouettes: a Graphical Aid to the Interpretation and Validation of Cluster Analysis". Computational and Applied Mathematics 20: 53–65. doi:10.1016/0377-0427(87)90125-7
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Silhouette extends AbstractEvaluator {

    private static final long serialVersionUID = -2195054290041907628L;
    private static String name = "Silhouette";

    public Silhouette() {
        dm = new EuclideanDistance();
        //some implementation (Matlab, sci-learn) compute silhouette without applying sqrt on distances
        ((EuclideanDistance) dm).setSqrt(false);
    }

    public Silhouette(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        //Silhouette Coefficent is only defined if number of labels
        // is 2 <= num_clusters <= num_samples - 1.
        if (clusters.size() == 1 || clusters.size() >= clusters.instancesCount()) {
            return Double.NaN; // coeficient is not defined for such clusterings
        }
        double score = 0.0;
        //for each cluster
        for (int i = 0; i < clusters.size(); i++) {
            score += clusterScore(clusters.get(i), clusters, i);
        }
        return (score / clusters.size());
    }

    /**
     * Score for single cluster
     *
     * @param clust
     * @param clusters
     * @param i
     * @return
     */
    public double clusterScore(Cluster clust, Clustering clusters, int i) {
        double clusterDist = 0.0;

        //calculate distance to all other objects in cluster
        for (int j = 0; j < clust.size(); j++) {
            clusterDist += instanceScore(clust, clusters, i, clust.instance(j));
        }
        return (clusterDist / clust.size());
    }

    /**
     *
     * @param clust
     * @param clusters
     * @param i        index of cluster
     * @param x
     * @return
     */
    public double instanceScore(Cluster clust, Clustering clusters, int i, Instance x) {
        Instance y;
        double a, b, dist, denom;
        a = 0;
        //we can't compute Silhouette for cluster with single item
        if (clust.size() > 1) {
            for (int k = 0; k < clust.size(); k++) {
                y = clust.instance(k);
                if (x.getIndex() != y.getIndex()) {
                    dist = dm.measure(x, y);
                    a += dist;
                }
            }
            //average distance
            a /= (clust.size() - 1);
        } else {
            //arbitrary defined value, according to the original paper
            a = 0.0;
        }

        //find minimal distance to other clusters
        b = minDistance(x, clusters, i);
        denom = Math.max(b, a);
        //avoid NaN, if possible
        if (denom == 0.0 || a == b) {
            return 0.0;
        }
        return (b - a) / denom;
    }

    /**
     * Minimal average distance of Instance x to other clusters. Cluster with
     * minimal avg distance is called the neighbor of object i.
     *
     * @param x
     * @param clusters
     * @param i        i-th cluster
     * @return
     */
    private double minDistance(Instance x, Clustering<? extends Cluster> clusters, int i) {
        double minDist = Double.MAX_VALUE;
        double clusterDist;
        Instance y;
        for (Cluster clust : clusters) {
            if (clust.getClusterId() != i) {
                clusterDist = 0;
                for (int j = 0; j < clust.size(); j++) {
                    y = clust.instance(j);
                    clusterDist += dm.measure(x, y);
                }
                clusterDist /= clust.size();
                if (clusterDist < minDist) {
                    minDist = clusterDist;
                }
            }
        }
        return minDist;
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return (score1 > score2);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return -1;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
