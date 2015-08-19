package org.clueminer.clustering.aggl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class NestedHC<E extends Instance, C extends Cluster<E>> {

    private ClusteringAlgorithm mapper;
    private ClusteringAlgorithm reducer;

    /**
     * Basically a clustering of a clustering. From a clustering created by a
     * partitioning algorithm, we derive a hierarchy
     *
     * @param clustering
     * @return
     */
    public Clustering<E, C> hierarchy(Clustering<E, C> clustering) {
        return null;
    }

    /**
     * Compute in parallel clustering of each cluster, then
     *
     * @param clustering
     * @return
     */
    public Clustering<E, C> compute(Clustering<E, C> clustering) {
        CyclicBarrier barrier = new CyclicBarrier(clustering.size());
        Props p = new Props();
        try {
            // Merger.merge(x, y, z);

            for (Cluster c : clustering) {
                //clustering clusters
                mapper.cluster(c, p);
                //TODO: implement
            }
            //force the thread to wait on the barrier.
            barrier.await();
            //merge the results
            //TODO: implement
        } catch (InterruptedException | BrokenBarrierException e) {
            Exceptions.printStackTrace(e);
        }

        return null;
    }

    public ClusteringAlgorithm getMapper() {
        return mapper;
    }

    public void setMapper(ClusteringAlgorithm mapper) {
        this.mapper = mapper;
    }

    public ClusteringAlgorithm getReducer() {
        return reducer;
    }

    public void setReducer(ClusteringAlgorithm reducer) {
        this.reducer = reducer;
    }

}
