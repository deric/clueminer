package org.clueminer.dataset.api;

/**
 * Same as @link{org.clueminer.math.Standardisation} but operates on top of
 * Datasets
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface DataStandardization<E extends Instance> {

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
    Dataset<E> optimize(Dataset<E> dataset);

}
