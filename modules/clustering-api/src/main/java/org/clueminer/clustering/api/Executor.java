package org.clueminer.clustering.api;

import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public interface Executor {

    AgglomerativeClustering getAlgorithm();

    void setAlgorithm(AgglomerativeClustering algorithm);

    /**
     * Run hierarchical clustering of rows in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, Props params);

    /**
     * Run hierarchical clustering of columns in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustColumns(Dataset<? extends Instance> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    Clustering<Cluster> clusterRows(Dataset<? extends Instance> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    DendrogramMapping clusterAll(Dataset<? extends Instance> dataset, Props params);

}
