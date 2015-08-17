package org.clueminer.distance;

import org.apache.commons.math3.util.FastMath;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class EuclideanDistance extends MinkowskiDistance {

    private static final String name = "Euclidean";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;
    private static final long serialVersionUID = 3142545695613722167L;
    private static EuclideanDistance instance;
    //whether compute SQRT(sum) or not
    private boolean sqrt = true;

    public EuclideanDistance() {
        this.power = 2;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Euclidean distance is quite frequently used, there's no need to create
     * instances all over again
     *
     * @return
     */
    public static EuclideanDistance getInstance() {
        if (instance == null) {
            instance = new EuclideanDistance();
        }
        return instance;
    }

    /**
     * Calculate distance between 2 columns in given matrix
     *
     * @param matrix
     * @param col1
     * @param col2
     * @return
     */
    @Override
    public double columns(Matrix matrix, int col1, int col2) {
        int n = matrix.rowsCount();
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            if ((!Double.isNaN(matrix.get(i, col1))) && (!Double.isNaN(matrix.get(i, col2)))) {
                sum += Math.pow((matrix.get(i, col1) - matrix.get(i, col2)), this.power);
            }
        }
        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    @Override
    public double rows(Matrix A, Matrix B, int e1, int e2) {
        int k = A.columnsCount();
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(e1, i))) && (!Double.isNaN(B.get(e2, i)))) {
                sum += Math.pow((A.get(e1, i) - B.get(e2, i)), this.power);
            }
        }
        if (sqrt) {
            return Math.sqrt(sum);
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
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            //should be faster
            sum += FastMath.pow(Math.abs(y.get(i) - x.get(i)), 2);
        }
        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += FastMath.pow(Math.abs(weights[i] * y.get(i) - weights[i] * x.get(i)), power);
        }

        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    public boolean isSqrt() {
        return sqrt;
    }

    public void setSqrt(boolean sqrt) {
        this.sqrt = sqrt;
    }

}
