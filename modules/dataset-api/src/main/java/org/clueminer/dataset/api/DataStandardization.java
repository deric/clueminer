package org.clueminer.dataset.api;

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
    Dataset<? extends Instance> optimize(Dataset<? extends Instance> dataset);

}
