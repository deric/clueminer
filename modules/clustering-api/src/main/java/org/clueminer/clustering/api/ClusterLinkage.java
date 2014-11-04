package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public interface ClusterLinkage extends Serializable {

    /**
     * Name identifier must be unique in application
     *
     * @return name of linkage
     */
    String getName();

    /**
     * Sets distance function which is used for computing similarity
     *
     * @param distanceMeasure
     */
    void setDistanceMeasure(DistanceMeasure distanceMeasure);

    /**
     * Calculates distance between two clusters
     *
     * @param cluster1
     * @param cluster2
     * @return
     */
    double distance(Dataset<Instance> cluster1, Dataset<Instance> cluster2);

    /**
     * Returns the similarity of two clusters according the specified linkage
     * function.
     *
     * @param similarityMatrix a matrix containing pair-wise similarity of each
     *                         data point in the entire set
     * @param cluster          the first cluster to be considered
     * @param toAdd            the second cluster to be considered
     *
     * @return the similarity of the two clusters
     */
    double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd);

    /**
     *
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return
     */
    double alphaA(int ma, int mb, int mq);

    /**
     *
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return
     */
    double alphaB(int ma, int mb, int mq);

    double beta();

    double gamma();
}
