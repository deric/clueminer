package org.clueminer.dataset.api;

import org.clueminer.math.Matrix;

/**
 * Same as @link{org.clueminer.math.Standardisation} but operates on top of
 * Datasets
 *
 * @author Tomas Barton
 */
public interface DataStandardization {

    /**
     *
     * @return name of the method
     */
    String getName();

    /**
     * Perform standardization of input data and return new Matrix with adjusted
     * values
     *
     * @param dataset
     * @return
     */
    Matrix optimize(Dataset<? extends Instance> dataset);

}
