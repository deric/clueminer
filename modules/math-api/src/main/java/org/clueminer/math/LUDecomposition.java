package org.clueminer.math;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface LUDecomposition extends Serializable {

    public Matrix getL();

    public Matrix getU();

    public int[] getPivot();

    public double[] getDoublePivot();

    public boolean isNonsingular();
}
