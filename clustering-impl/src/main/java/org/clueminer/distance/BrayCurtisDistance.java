package org.clueminer.distance;

import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;

/**
 * FIXME cause problems with optimizing leaves order
 *
 * @author Tomas Barton
 */
//@ServiceProvider(service = AbstractDistance.class)
public class BrayCurtisDistance extends SymmetricDistance {

    private static final long serialVersionUID = -9160673893231083803L;
    private static String name = "Bray-Curtis";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;

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
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < n; i++) {
            if ((!Double.isNaN(matrix.get(i, col1))) && (!Double.isNaN(matrix.get(i, col2)))) {
                numerator += Math.abs((matrix.get(i, col1) - matrix.get(i, col2)));
                denominator += matrix.get(i, col1) + matrix.get(i, col2);
            }
        }
        return (numerator / denominator);
    }

    public double rows(Matrix A, Matrix B, int e1, int e2) {
        int k = A.columnsCount();
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(e1, i))) && (!Double.isNaN(B.get(e2, i)))) {
                numerator += Math.abs((A.get(e1, i) - B.get(e2, i)));
                denominator += A.get(e1, i) + B.get(e2, i);
            }
        }
        return (numerator / denominator);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        int k = x.size();
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(x.get(i))) && (!Double.isNaN(y.get(i)))) {
                numerator += Math.abs((x.get(i) - y.get(i)));
                denominator += x.get(i) + y.get(i);
            }
        }
        return (numerator / denominator);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public boolean useTreeHeight() {
        return true;
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
}
