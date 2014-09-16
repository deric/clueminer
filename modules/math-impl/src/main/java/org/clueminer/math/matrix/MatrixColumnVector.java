package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.Vector;
import org.clueminer.math.impl.AbstractDoubleVector;

/**
 * A reference to column in a matrix to allow abstraction on column based matrix
 * operations.
 *
 * @author Tomas Barton
 */
public class MatrixColumnVector extends AbstractDoubleVector implements MatrixVector {

    private final Matrix matrix;
    private final int column;

    public MatrixColumnVector(Matrix mat, int j) {
        this.matrix = mat;
        this.column = j;
    }

    @Override
    public double add(int index, double delta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param index
     * @return value in index-th column
     */
    @Override
    public double get(int index) {
        return matrix.get(index, column);
    }

    @Override
    public Double getValue(int index) {
        return get(index);
    }

    @Override
    public void set(int index, double value) {
        matrix.set(index, column, value);
    }

    @Override
    public double[] toArray() {
        double[] res = new double[matrix.columnsCount()];
        for (int i = 0; i < res.length; i++) {
            res[i] = matrix.get(i, column);

        }
        return res;
    }

    @Override
    public int size() {
        return matrix.columnsCount();
    }

    @Override
    public void set(int index, Number value) {
        matrix.set(index, column, value.doubleValue());
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> duplicate() {
        return new MatrixColumnVector(matrix, column);
    }
}
