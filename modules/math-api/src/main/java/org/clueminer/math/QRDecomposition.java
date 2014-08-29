package org.clueminer.math;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface QRDecomposition extends Serializable {

    /**
     * Generate and return the (economy-sized) orthogonal factor
     *
     * @return Q
     */
    public Matrix getQ();

    /**
     * Return the upper triangular factor
     *
     * @return R
     */
    public Matrix getR();

    /**
     * Return the Householder vectors
     *
     * @return Lower trapezoidal matrix whose columns define the reflections
     */
    public Matrix getH();
}
