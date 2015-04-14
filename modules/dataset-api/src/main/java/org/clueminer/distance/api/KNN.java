package org.clueminer.distance.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * k-nearest neighbors algorithm
 *
 * @author Tomas Barton
 */
public interface KNN {

    /**
     * Unique algorithm identifier
     *
     * @return the name of algorithm implementation
     */
    String getName();

    /**
     * Get /k/ nearest neighbors to given /idx/
     *
     * @param idx
     * @param k
     * @param dataset
     * @param params  key-value configuration
     * @return
     */
    int[] nnIds(int idx, int k, Dataset<? extends Instance> dataset, Props params);

    /**
     *
     * @param idx
     * @param k
     * @param dataset
     * @param params  key-value configuration (e.g. distance metric)
     * @return k nearest instances to given /idx/
     */
    Instance[] nn(int idx, int k, Dataset<? extends Instance> dataset, Props params);
}
