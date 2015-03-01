package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
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
        dm = EuclideanDistance.getInstance();
    }

    public Silhouette(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double score = 0;
        Cluster clust;
        //for each cluster
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            score += clusterScore(clust, clusters, i);
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
        for (int k = 0; k < clust.size(); k++) {
            y = clust.instance(k);
            if (x.getIndex() != y.getIndex()) {
                dist = dm.measure(x, y);
                a += dist;
            }
        }
        //average distance
        a /= clust.size() - 1;

        //find minimal distance to other clusters
        b = minDistance(x, clusters, i);
        denom = Math.max(b, a);
        //avoid NaN, if possible
        if (denom == 0.0 || a == b) {
            return 0.0;
        }
        if (a < b) {
            return 1 - a / b;
        }
        return (b / a) - 1;
        //return (b - a) / denom;
    }

    /**
     * Minimal average distance of Instance x to other clusters
     *
     * @param x
     * @param clusters
     * @param i        i-th cluster
     * @return
     */
    private double minDistance(Instance x, Clustering clusters, int i) {
        double minDist = Double.MAX_VALUE;
        double clusterDist;
        Instance y;
        for (int k = 0; k < clusters.size(); k++) {
            if (k != i) {
                Dataset clust = clusters.get(k);
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

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
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
}
