package org.clueminer.math;

/**
 *
 * @author Tomas Barton
 */
public interface SparseMatrix extends Matrix {

    /**
     * Returns the column as a sparse vector. Whether updates to the vector are
     * written through to the backing matrix is left open to the implementation.
     *
     * @param column The column to return a {@code DoubleVector} for
     *
     * @return A {@code SparseVector} representing the column at {@code column}
     */
    SparseVector getColumnSparseVector(int column);

    /**
     * Returns the row as a sparse vector. Whether updates to the vector are
     * written through to the backing matrix is left open to the implementation.
     *
     * @param row the index of row to return
     *
     * @return A {@code SparseVector} of the row's data
     */
    SparseVector getRowSparseVector(int row);
}
