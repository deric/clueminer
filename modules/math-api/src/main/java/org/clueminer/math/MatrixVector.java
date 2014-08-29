package org.clueminer.math;

/**
 * Represents either row or column of a matrix, all modifications are directly
 * written to original Matrix
 *
 * @author Tomas Barton
 */
public interface MatrixVector extends DoubleVector {

    
    public Matrix getMatrix();
}
