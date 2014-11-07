package org.clueminer.clustering.aggl.linkage;

import java.util.Set;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterLinkage.class)
public class WardsLinkage extends AbstractLinkage implements ClusterLinkage {

    public static final String name = "Ward's Linkage";

    public WardsLinkage() {
        super(EuclideanDistance.getInstance());
    }

    public WardsLinkage(DistanceMeasure dist) {
        super(dist);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double distance(Cluster<? extends Instance> cluster1, Cluster<? extends Instance> cluster2) {
        //Instance centroid1 = cluster1.

        //return (2 * cluster1.size() * cluster2.size()) / (cluster1.size() + cluster2.size()) * similaritySum;
        return 0;
    }

    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        throw new UnsupportedOperationException("Not supported yet.");
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

}
