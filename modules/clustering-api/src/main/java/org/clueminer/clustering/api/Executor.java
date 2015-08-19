package org.clueminer.clustering.api;

import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface Executor<E extends Instance, C extends Cluster<E>> {

    ClusteringAlgorithm<E, C> getAlgorithm();

    void setAlgorithm(ClusteringAlgorithm<E, C> algorithm);

    /**
     * Run hierarchical clustering of rows in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustRows(Dataset<E> dataset, Props params);

    /**
     * Run hierarchical clustering of columns in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustColumns(Dataset<E> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    Clustering<E, C> clusterRows(Dataset<E> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    DendrogramMapping clusterAll(Dataset<E> dataset, Props params);

}
