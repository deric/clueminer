package org.clueminer.clustering.aggl.linkage;

import java.util.Arrays;
import java.util.Set;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Median linkage is a variation of {@link AverageLinkage}, this method should
 * be less sensitive to outliers because we consider distance between existing
 * data points and not just "virtual ones".
 *
 * A.K.A. Weighted Pair Group Method using Centroids
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = ClusterLinkage.class)
public class MedianLinkage<E extends Instance> extends AbstractLinkage<E> implements ClusterLinkage<E> {

    private static final long serialVersionUID = 7942079385178130304L;
    public static final String name = "Median";

    public MedianLinkage() {
        super(EuclideanDistance.getInstance());
    }

    public MedianLinkage(Distance dm) {
        super(dm);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Clusters will be compared using the similarity of the computed median
     * data point for each cluster
     *
     * @param cluster1 first cluster
     * @param cluster2 second cluster
     * @return distance between clusters computed with current distance metric
     */
    @Override
    public double distance(Cluster<E> cluster1, Cluster<E> cluster2) {
        return distanceMeasure.measure(cluster1.getCentroid(), cluster2.getCentroid());
    }

    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        double[] similarities = new double[cluster.size() * toAdd.size()];
        int index = 0;
        for (int i : cluster) {
            for (int j : toAdd) {
                similarities[index++] = similarityMatrix.get(i, j);
            }
        }
        Arrays.sort(similarities);
        return similarities[similarities.length / 2];
    }

    @Override
    public double alphaA(int ma, int mb, int mq) {
        return 0.5;
    }

    @Override
    public double alphaB(int ma, int mb, int mq) {
        return 0.5;
    }

    @Override
    public double beta(int ma, int mb, int mq) {
        return -0.25;
    }

    @Override
    public double gamma() {
        return 0;
    }

    @Override
    public boolean usesCentroids() {
        return true;
    }

    @Override
    public E updateCentroid(int ma, int mb, E centroidA, E centroidB, Dataset<E> dataset) {
        E res = dataset.builder().build(dataset.attributeCount());
        for (int i = 0; i < dataset.attributeCount(); i++) {
            res.set(i, (centroidA.get(i) + centroidB.get(i)) / 2.0);
        }
        return res;
    }

    @Override
    public double centroidDistance(int ma, int mb, E centroidA, E centroidB) {
        return distanceMeasure.measure(centroidA, centroidB);
    }
}
