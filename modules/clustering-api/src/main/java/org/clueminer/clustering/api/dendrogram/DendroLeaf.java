package org.clueminer.clustering.api.dendrogram;

import org.clueminer.dataset.api.DataVector;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface DendroLeaf<T extends DataVector> extends DendroNode {

    /**
     * Corresponding associated data row/column vector from the dataset (valid
     * only if node is a leaf)
     *
     * @return Instance
     */
    T getData();

    void setData(T data);

}
