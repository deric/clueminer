package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Manhattan (or city block distance) is the distance you would have to walk
 * between two points in a city with regular grid schema (like Manhattan)
 *
 * Generalization of this distance metric is Minkowski distance, in this case (n
 * = 1)
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class ManhattanDistance extends SymmetricDistance {

    private static String name = "Manhattan";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;
    private static final long serialVersionUID = 3287053682318427095L;

    @Override
    public String getName() {
        return name;
    }

    public double columns(Matrix matrix, int e1, int e2) {
        int n, j;
        double sum = 0.0;
        n = matrix.rowsCount();
        for (j = 0; j < n; j++) {
            if ((!Double.isNaN(matrix.get(j, e1))) && (!Double.isNaN(matrix.get(j, e2)))) {
                sum += Math.abs(matrix.get(j, e1) - matrix.get(j, e2));
            }
        }
        return sum;
    }

    public double rows(Matrix A, Matrix B, int g1, int g2) {
        int j;
        double sum = 0.0;
        int n = A.columnsCount();
        for (j = 0; j < n; j++) {
            if ((!Double.isNaN(A.get(g1, j))) && (!Double.isNaN(B.get(g2, j)))) {
                sum += Math.abs(A.get(g1, j) - A.get(g2, j));
            }
        }
        return sum;
    }

    @Override
    public double vector(DoubleVector A, DoubleVector B) {
        int j;
        double sum = 0.0;
        int n = A.size();
        for (j = 0; j < n; j++) {
            if ((!Double.isNaN(A.get(j))) && (!Double.isNaN(B.get(j)))) {
                sum += Math.abs(A.get(j) - A.get(j));
            }
        }
        return sum;
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
    public double measure(Instance x, Instance y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double measure(Instance x, Instance y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return Double.MAX_VALUE;
    }
}
