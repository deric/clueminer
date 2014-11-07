package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.LinkageFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Dunn's index should be maximized
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class DunnIndex extends ClusterEvaluator {

    private static final long serialVersionUID = -6973489229802690101L;
    private static final String name = "Dunn index";

    public DunnIndex() {
        dm = EuclideanDistance.getInstance();
    }

    public DunnIndex(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        int k = clusters.size();
        if (k < 2) {
            //doesn't make much sense to compute index for one cluster
            return Double.NaN;
        }

        double maxIntraClusterdist = Double.MIN_VALUE, temp;
        double minClusterDistance = Double.MAX_VALUE;
        Cluster<? extends Instance> clusterX, clusterY;
        ClusterLinkage link = LinkageFactory.getInstance().getProvider("Single Linkage");
        link.setDistanceMeasure(dm);

        for (int i = 0; i < clusters.size(); i++) {
            clusterX = clusters.get(i);
            //find maximal distance in between each cluster
            temp = maxIntraClusterDistance(clusterX);
            if (temp > maxIntraClusterdist) {
                maxIntraClusterdist = temp;
            }
            //@todo verify implementation
            for (int j = i + 1; j < clusters.size(); j++) {
                clusterY = clusters.get(j);
                /*
                 * finding minimal distance between objects in both clusters
                 * corresponds to single linkage distance
                 */
                temp = link.distance(clusterX, clusterY);
                if (temp < minClusterDistance) {
                    minClusterDistance = temp;
                }
            }

        }
        return minClusterDistance / maxIntraClusterdist;
    }

    public double maxIntraClusterDistance(Dataset<? extends Instance> cluster) {
        double max = Double.MIN_VALUE;
        Instance x, y;
        double dist;
        for (int i = 0; i < cluster.size(); i++) {
            x = cluster.instance(i);
            for (int j = i + 1; j < cluster.size(); j++) {
                y = cluster.instance(j);
                dist = dm.measure(x, y);
                if (dist > max) {
                    max = dist;
                }
            }
        }
        return max;
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

    @Override
    public boolean isMaximized() {
        return true;
    }
}
