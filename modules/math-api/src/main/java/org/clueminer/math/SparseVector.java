package org.clueminer.math;


/**
 * An interface for {@code Vector} implementations whose values are sparse and
 * that support access to only those indices with non-zero values.
 *
 * @author Tomas Barton
 */
public interface SparseVector<T extends Number> extends Vector<T> {

    /**
     * Returns all the indices whose values are non-zero
     */
    int[] getNonZeroIndices();
}
