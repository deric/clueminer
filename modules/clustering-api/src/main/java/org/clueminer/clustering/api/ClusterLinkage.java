/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;

/**
 * Interface for computing distances between two clusters.
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ClusterLinkage<E extends Instance> extends Serializable {

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
    void setDistanceMeasure(Distance distanceMeasure);

    /**
     * Naive method of calculating distance between two clusters. Usually very
     * expensive.
     *
     * @param cluster1
     * @param cluster2
     * @return
     */
    double distance(Cluster<E> cluster1, Cluster<E> cluster2);

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
     * Some linkage methods (median, Ward's requires computation of centroids)
     *
     * @return when true centroid should be updated after each merge
     */
    boolean usesCentroids();

    /**
     * Update centroid of newly merged cluster. Supported only by some methods
     * (Median, Centroid, Ward's)
     *
     * @param ma
     * @param mb
     * @param centroidA
     * @param centroidB
     * @param dataset
     *
     * @return centroid of newly merged cluster
     */
    E updateCentroid(int ma, int mb, E centroidA, E centroidB, Dataset<E> dataset);

    /**
     * Compute distances between centroids
     *
     * @param ma
     * @param mb
     * @param centroidA
     * @param centroidB
     * @return
     */
    double centroidDistance(int ma, int mb, E centroidA, E centroidB);

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
