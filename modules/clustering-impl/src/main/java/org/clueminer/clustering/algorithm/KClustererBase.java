package org.clueminer.clustering.algorithm;

import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class KClustererBase<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    @Override
    public Distance getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(Distance dm) {
        this.distanceFunction = dm;
    }

    /**
     * Convenient helper method. A list of lists to represent a cluster may be
     * desirable. In such a case, this method will take in an array of cluster
     * assignments, and return a list of lists.
     *
     * @param assignments cluster assignments
     * @param dataset the original data set, with data in the same order as
     * was
     * used to create the assignments array
     * @return a final clustering
     */
    public static Clustering fromAssignment(Assignment assignments, Dataset dataset) {
        //an assignment class with number of distinct values might be useful
        Clustering clusterings = new ClusterList<>(assignments.distinct());

        int clusterId;
        Cluster curr;
        for (int i = 0; i < dataset.size(); i++) {
            clusterId = assignments.assigned(i);

            if (!clusterings.hasAt(clusterId)) {
                curr = clusterings.createCluster(clusterId);
            } else {
                curr = clusterings.get(clusterId);
            }
            curr.add(dataset.get(i));
        }

        return clusterings;
    }

}
