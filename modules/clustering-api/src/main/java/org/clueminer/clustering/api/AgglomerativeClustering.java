package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public interface AgglomerativeClustering extends ClusteringAlgorithm {

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param pref
     * @return
     */
    HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref);

    /**
     * Some implementation can not support certain linkage types
     *
     * @param linkage name of linkage method
     * @return
     */
    boolean isLinkageSupported(String linkage);

}
