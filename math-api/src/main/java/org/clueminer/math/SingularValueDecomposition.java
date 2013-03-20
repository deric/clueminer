package org.clueminer.math;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface SingularValueDecomposition extends Serializable {

    public double[] getSingularValues();

    public int rank();

    public double cond();

    public double norm2();

    public Matrix getS();

    public Matrix getV();

    public Matrix getU();
}
