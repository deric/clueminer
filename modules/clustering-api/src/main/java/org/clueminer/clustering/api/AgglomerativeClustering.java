package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface AgglomerativeClustering<T extends Instance> extends ClusteringAlgorithm<T> {

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param pref
     * @return
     */
    HierarchicalResult hierarchy(Dataset<T> dataset, Props pref);

    /**
     * Some implementation can not support certain linkage types
     *
     * @param linkage name of linkage method
     * @return
     */
    boolean isLinkageSupported(String linkage);

}
