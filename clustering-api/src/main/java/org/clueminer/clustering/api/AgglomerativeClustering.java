package org.clueminer.clustering.api;

import java.util.prefs.Preferences;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.AlgorithmParameters;

/**
 *
 * @author Tomas Barton
 */
public interface AgglomerativeClustering extends ClusteringAlgorithm {

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, AlgorithmParameters params);

    /**
     *
     * @param input
     * @param dataset
     * @param params
     * @return
     */
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, AlgorithmParameters params);

    public HierarchicalResult hierarchy(Matrix matrix, Preferences props);

}
