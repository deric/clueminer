package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Returns greatest difference between two points, sometimes called Chebyshev
 * distance.
 *
 * Chebyshev Distance is the L<sub>&#8734;</sub> norm.
  *
 * @author Tomas Barton
 */
@ServiceProvider(service = DistanceMeasure.class)
public class SupremumDistance extends AbstractDistance {

    private static final String name = "Supremum (Chebyshev)";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;
    private static final long serialVersionUID = 5537883377318684946L;

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
        double max = 0.0;
        double diff;
        for (int i = 0; i < n; i++) {
            if ((!Double.isNaN(matrix.get(i, col1))) && (!Double.isNaN(matrix.get(i, col2)))) {
                diff = Math.abs((matrix.get(i, col1) - matrix.get(i, col2)));
                if (diff > max) {
                    max = diff;
                }
            }
        }
        return max;
    }

    public double rows(Matrix A, Matrix B, int e1, int e2) {
        int k = A.columnsCount();
        double max = 0.0;
        double diff;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(e1, i))) && (!Double.isNaN(B.get(e2, i)))) {
                diff = Math.abs((A.get(e1, i) - B.get(e2, i)));
                if (diff > max) {
                    max = diff;
                }
            }
        }
        return max;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        int k = x.size();
        double max = 0.0;
        double diff;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(x.get(i))) && (!Double.isNaN(y.get(i)))) {
                diff = Math.abs((x.get(i) - y.get(i)));
                if (diff > max) {
                    max = diff;
                }
            }
        }
        return max;
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
    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMinValue() {
        return 0.0;
    }

    @Override
    public double getMaxValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }
}
