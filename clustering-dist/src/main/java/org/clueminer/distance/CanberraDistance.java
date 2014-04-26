package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class CanberraDistance extends SymmetricDistance {

    private static final String name = "Canberra";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;
    private static final long serialVersionUID = 1585719949710328924L;

    @Override
    public String getName() {
        return name;
    }

    /**
     * Calculate distance between 2 columns in given matrix
     *
     * @param matrix
     * @param col1
     * @param col2
     * @return
     */
    public double columns(Matrix matrix, int col1, int col2) {
        int n = matrix.rowsCount();
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            if ((!Double.isNaN(matrix.get(i, col1))) && (!Double.isNaN(matrix.get(i, col2)))) {
                sum += Math.abs((matrix.get(i, col1) - matrix.get(i, col2))) / (Math.abs(matrix.get(i, col1)) + Math.abs(matrix.get(i, col2)));
            }
        }
        return (float) sum;
    }

    public double rows(Matrix A, Matrix B, int e1, int e2) {
        int k = A.columnsCount();
        int n = 0;
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(e1, i))) && (!Double.isNaN(B.get(e2, i)))) {
                sum += Math.abs((A.get(e1, i) - B.get(e2, i))) / (Math.abs(A.get(e1, i)) + Math.abs(B.get(e2, i)));
                n++;
            }
        }
        return (float) sum;
    }

    @Override
    public float getSimilarityFactor() {
        return similarityFactor;
    }

    @Override
    public int getNodeOffset() {
        return offset;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMinValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMaxValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSubadditive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIndiscernible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
