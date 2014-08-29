package org.clueminer.math;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface EigenvalueDecomposition extends Serializable {

    public Matrix getV();

    public double[] getRealEigenvalues();

    /**
     * Return the imaginary parts of the eigenvalues
     *
     * @return imag(diag(D))
     */
    public double[] getImagEigenvalues();

    /**
     * Return the block diagonal eigenvalue matrix
     *
     * @return D
     */
    public Matrix getD();
}
