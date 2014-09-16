package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.Vector;
import org.clueminer.math.impl.AbstractDoubleVector;

/**
 * A proxy to matrix row to allow row based operations in a matrix.
 *
 * @author Tomas Barton
 */
public class MatrixRowVector extends AbstractDoubleVector implements MatrixVector {

    private final Matrix matrix;
    private final int row;

    public MatrixRowVector(Matrix mat, int i) {
        this.matrix = mat;
        this.row = i;
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
        return matrix.get(row, index);
    }

    @Override
    public Double getValue(int index) {
        return get(index);
    }

    @Override
    public void set(int index, double value) {
        matrix.set(row, index, value);
    }

    @Override
    public double[] toArray() {
        double[] res = new double[matrix.columnsCount()];
        for (int i = 0; i < res.length; i++) {
            res[i] = matrix.get(row, i);

        }
        return res;
    }

    @Override
    public int size() {
        return matrix.columnsCount();
    }

    @Override
    public void set(int index, Number value) {
        matrix.set(row, index, value.doubleValue());
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    @Override
    public Vector<Double> duplicate() {
        return new MatrixRowVector(matrix, row);
    }
}
