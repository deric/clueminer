package org.clueminer.evaluation;

import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class Silhouette extends ClusterEvaluator {

    private static final long serialVersionUID = -2195054290041907628L;
    private static String name = "Silhouette";

    public Silhouette() {
        dm = new EuclideanDistance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double score = 0;
        double a, b, dist, clusterDist;
        Instance x, y;
        Dataset clust;
        //for each cluster
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            clusterDist = 0;
            //calculate distance to all other objects in cluster
            for (int j = 0; j < clust.size(); j++) {
                x = clust.instance(j);
                a = 0;
                for (int k = 0; k < clust.size(); k++) {
                    if (j != k) {
                        y = clust.instance(k);
                        dist = dm.measure(x, y);
                        a += dist;
                    }
                }
                //average distance
                a /= clust.size() - 1;

                //find minimal distance to other clusters
                b = minDistance(x, clusters, i);
                clusterDist += (b - a) / Math.max(b, a);
            }
            score += (clusterDist / clust.size());
        }
        return (score / clusters.size());
    }

    /**
     * Minimal average distance of Instance x to other clusters
     *
     * @param x
     * @param clusters
     * @param i
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return (score1 > score2);
    }
}
