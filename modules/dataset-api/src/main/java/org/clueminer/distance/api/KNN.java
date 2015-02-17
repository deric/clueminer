package org.clueminer.distance.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * k-nearest neighbors algorithm
 *
 * @author Tomas Barton
 */
public interface KNN {

    /**
     * Get /k/ nearest neighbors to given /idx/
     *
     * @param idx
     * @param k
     * @param dataset
     * @return
     */
    int[] nnIds(int idx, int k, Dataset<? extends Instance> dataset);

    /**
     *
     * @param idx
     * @param k
     * @param dataset
     * @return k nearest instances to given /idx/
     */
    Instance[] nn(int idx, int k, Dataset<? extends Instance> dataset);
}
