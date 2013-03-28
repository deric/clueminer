package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.Matrix;

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
    public double vector(DoubleVector A, DoubleVector B) {
        int k = A.size();
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(i))) && (!Double.isNaN(B.get(i)))) {
                numerator += Math.abs((A.get(i) - B.get(i)));
                denominator += A.get(i) + B.get(i);
            }
        }
        return (numerator / denominator);
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
    public double measure(Instance x, Instance y) {
        throw new UnsupportedOperationException("Not supported yet.");
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
