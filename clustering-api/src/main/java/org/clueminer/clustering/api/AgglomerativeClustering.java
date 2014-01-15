package org.clueminer.clustering.api;

import java.util.prefs.Preferences;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

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
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Preferences pref);

    /**
     *
     * @param input
     * @param dataset
     * @param pref
     * @return
     */
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, Preferences pref);

    public HierarchicalResult hierarchy(Matrix matrix, Preferences props);

}
