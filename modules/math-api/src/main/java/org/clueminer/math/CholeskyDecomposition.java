package org.clueminer.math;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface CholeskyDecomposition extends Serializable {

    public boolean isSPD();

    /**
     * Return triangular factor.
     *
     * @return L
     */
    public Matrix getL();

    /**
     * Solve A*X = B
     *
     * @param B A JMatrix with as many rows as A and any number of columns.
     * @return X so that L*L'*X = B
     * @exception IllegalArgumentException JMatrix row dimensions must agree.
     * @exception RuntimeException JMatrix is not symmetric positive definite.
     */
    public Matrix solve(Matrix B);
}
