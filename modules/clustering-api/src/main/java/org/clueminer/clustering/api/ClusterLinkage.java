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
     * We are merging cluster A and cluster B to make a new cluster R. Cluster Q
     * is one of remaining cluster to which we update distance.
     *
     * Lance, G. N. and Williams, W. T.. "A general theory of classificatory
     * sorting strategies 1. Hierarchical systems." The Computer Journal 9 , no.
     * 4 (1967):373-380.
     *
     *
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return Lance-Williams coefficient alpha_A
     */
    double alphaA(int ma, int mb, int mq);

    /**
     * We are merging cluster A and cluster B to make a new cluster R. Cluster Q
     * is one of remaining cluster to which we update distance.
     *
     * Lance, G. N. and Williams, W. T.. "A general theory of classificatory
     * sorting strategies 1. Hierarchical systems." The Computer Journal 9 , no.
     * 4 (1967):373-380.
     *
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return Lance-Williams coefficient alpha_B
     */
    double alphaB(int ma, int mb, int mq);

    /**
     * We are merging cluster A and cluster B to make a new cluster R. Cluster Q
     * is one of remaining cluster to which we update distance.
     *
     * Lance, G. N. and Williams, W. T.. "A general theory of classificatory
     * sorting strategies 1. Hierarchical systems." The Computer Journal 9 , no.
     * 4 (1967):373-380.
     *
     * @param ma size of cluster A
     * @param mb size of cluster B
     * @param mq size of cluster Q
     * @return Lance-Williams coefficient beta
     */
    double beta(int ma, int mb, int mq);

    /**
     * Lance, G. N. and Williams, W. T.. "A general theory of classificatory
     * sorting strategies 1. Hierarchical systems." The Computer Journal 9 , no.
     * 4 (1967):373-380.
     *
     * @return Lance-Williams coefficient gamma
     */
    double gamma();
}
