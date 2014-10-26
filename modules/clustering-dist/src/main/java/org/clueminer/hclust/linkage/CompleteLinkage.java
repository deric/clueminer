package org.clueminer.hclust.linkage;

import java.util.Set;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * We consider the distance between one cluster and another to be equal to the
 * greatest distance from any member of first cluster to any member of second
 * cluster. Sometimes it's called the maximum method or the diameter method.
 *
 * Complete Link or MAX or CLIQUE
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterLinkage.class)
public class CompleteLinkage extends AbstractLinkage implements ClusterLinkage {

    private static final long serialVersionUID = -852898753773273748L;
    public static final String name = "Complete Linkage";

    public CompleteLinkage() {
        super(new EuclideanDistance());
    }

    public CompleteLinkage(DistanceMeasure dm) {
        super(dm);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double distance(Dataset<Instance> cluster1, Dataset<Instance> cluster2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * For the complete link or MAX version of hierarchical clustering, the
     * proximity of two clusters is defined as the maximum of the distance
     * (minimum of the similarity) between any two points in the two different
     * clusters.
     *
     * @param similarityMatrix
     * @param cluster
     * @param toAdd
     * @return
     */
    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        double maximumDistance = Double.MIN_VALUE;
        for (int i : cluster) {
            for (int j : toAdd) {
                double s = similarityMatrix.get(i, j);
                if (distanceMeasure.compare(maximumDistance, s)) {
                    maximumDistance = s;
                }
            }
        }
        return maximumDistance;
    }

    @Override
    public double alphaA() {
        return 0.5;
    }

    @Override
    public double alphaB() {
        return 0.5;
    }

    @Override
    public double beta() {
        return 0.0;
    }

    @Override
    public double gamma() {
        return 0.5;
    }
}
