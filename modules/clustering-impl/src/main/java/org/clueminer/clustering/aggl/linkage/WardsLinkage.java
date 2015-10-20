package org.clueminer.clustering.aggl.linkage;

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
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = ClusterLinkage.class)
public class WardsLinkage<E extends Instance> extends AbstractLinkage<E> implements ClusterLinkage<E> {

    public static final String name = "Wards";
    private static final long serialVersionUID = 1675876780777905831L;

    public WardsLinkage() {
        super(EuclideanDistance.getInstance());
    }

    public WardsLinkage(Distance dist) {
        super(dist);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double distance(Cluster<E> cluster1, Cluster<E> cluster2) {
        Instance centroid1 = cluster1.getCentroid();
        Instance centroid2 = cluster2.getCentroid();

        double diff = distanceMeasure.measure(centroid1, centroid2);

        return (cluster1.size() * cluster2.size()) / (cluster1.size() + cluster2.size()) * diff;
    }

    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public double alphaA(int ma, int mb, int mq) {
        return (double) (ma + mq) / (ma + mb + mq);
    }

    @Override
    public double alphaB(int ma, int mb, int mq) {
        return (double) (mb + mq) / (ma + mb + mq);
    }

    @Override
    public double beta(int ma, int mb, int mq) {
        return (double) -mq / (ma + mb + mq);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double centroidDistance(int ma, int mb, E centroidA, E centroidB) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
