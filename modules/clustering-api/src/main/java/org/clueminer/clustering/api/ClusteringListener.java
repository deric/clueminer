package org.clueminer.clustering.api;

import java.util.EventListener;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ClusteringListener<E extends Instance> extends EventListener {

    /**
     * Triggered when starts executor starts data clustering
     *
     * @param dataset data to be clustered
     * @param params  parameters of the clustering algorithm
     */
    void clusteringStarted(Dataset<E> dataset, Props params);

    /**
     * Triggered when clustering finishes
     *
     * @param clust
     */
    void clusteringChanged(Clustering<E, Cluster<E>> clust);

    void resultUpdate(HierarchicalResult<E> hclust);
}
