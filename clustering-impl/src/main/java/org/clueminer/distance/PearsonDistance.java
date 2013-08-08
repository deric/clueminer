package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Pearson correlation is a measure of the similarity in shape of the two
 * expression patterns. Two functions which differs just in an amplitude would
 * have still perfect Pearson correlation equal to 1.
 *
 * The Pearson correlation coefficient is always between -1 and 1, with 1
 * meaning that the two series are identical, 0 meaning they are completely
 * uncorrelated, and -1 meaning they are perfect opposites
 *
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class PearsonDistance extends SymmetricDistance {

    private static String name = "Pearson";
    private static float similarityFactor = -1.0f;
    private static int offset = 1;
    private static final long serialVersionUID = -5861415196767414635L;

    @Override
    public String getName() {
        return name;
    }

    public double columns(Matrix matrix, int e1, int e2) {
        double TINY = Double.MIN_VALUE;
        int n, j, k;
        double xt, yt;
        //standard deviation
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;
        double ax = 0.0;
        double ay = 0.0;
        k = matrix.rowsCount();
        n = 0;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(matrix.get(j, e1))) && (!Double.isNaN(matrix.get(j, e2)))) {
                ax += matrix.get(j, e1);
                ay += matrix.get(j, e2);
                n++;
            }
        }
        ax /= n;
        ay /= n;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(matrix.get(j, e1))) && (!Double.isNaN(matrix.get(j, e2)))) {
                xt = matrix.get(j, e1) - ax;
                yt = matrix.get(j, e2) - ay;
                sxx += xt * xt;
                syy += yt * yt;
                sxy += xt * yt;
            }
        }
        return (sxy / (Math.sqrt(sxx * syy) + TINY));
    }

    public double rows(Matrix X, Matrix Y, int g1, int g2) {
        double[] arrX = X.getColumnPackedCopy();
        double[] arrY = Y.getColumnPackedCopy(); //@TODO check this
        int nArrSize = X.columnsCount();

        double dblXY = 0f;
        double dblX = 0f;
        double dblXX = 0f;
        double dblY = 0f;
        double dblYY = 0f;

        double v_1, v_2;
        int iValidValCount = 0;
        for (int i = 0; i < nArrSize; i++) {
            v_1 = arrX[i];
            v_2 = arrY[i];
            if (Double.isNaN(v_1) || Double.isNaN(v_2)) {
                continue;
            }
            iValidValCount++;
            dblXY += v_1 * v_2;
            dblXX += v_1 * v_1;
            dblYY += v_2 * v_2;
            dblX += v_1;
            dblY += v_2;
        }
        if (iValidValCount == 0) {
            return 0f;
        }

        //Allows for a comparison of two 'flat' genes (genes with no variability in their
        // expression values), ie. 0, 0, 0, 0, 0
        boolean nonFlat = false;
        NON_FLAT_CHECK:
        for (int j = 1; j < nArrSize; j++) {
            if ((!Double.isNaN(arrX[j])) && (!Double.isNaN(arrY[j]))) {
                if (arrX[j] != arrX[j - 1]) {
                    nonFlat = true;
                    break NON_FLAT_CHECK;
                }
                if (arrY[j] != arrY[j - 1]) {
                    nonFlat = true;
                    break NON_FLAT_CHECK;
                }
            }
        }

        if (nonFlat == false) {
            return 1.0f;
        }

        double dblAvgX = dblX / iValidValCount;
        double dblAvgY = dblY / iValidValCount;
        double dblUpper = dblXY - dblX * dblAvgY - dblAvgX * dblY + dblAvgX * dblAvgY * ((double) iValidValCount);
        double p1 = (dblXX - dblAvgX * dblX * 2d + dblAvgX * dblAvgX * ((double) iValidValCount));
        double p2 = (dblYY - dblAvgY * dblY * 2d + dblAvgY * dblAvgY * ((double) iValidValCount));
        double dblLower = p1 * p2;
        return (dblUpper / (Math.sqrt(dblLower) + Double.MIN_VALUE));
    }

    @Override
    public double vector(DoubleVector x, DoubleVector y) {
        double TINY = Double.MIN_VALUE;
        int n, j, k;
        double xt, yt;
        //standard deviation
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;
        double ax = 0.0;
        double ay = 0.0;
        k = x.size();
        n = 0;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                ax += x.get(j);
                ay += y.get(j);
                n++;
            }
        }
        ax /= n;
        ay /= n;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                xt = x.get(j) - ax;
                yt = y.get(j) - ay;
                sxx += xt * xt;
                syy += yt * yt;
                sxy += xt * yt;
            }
        }
        return (sxy / (Math.sqrt(sxx * syy) + TINY));
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

    /**
     * Return true if X is better than Y
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean compare(double x, double y) {
        return (x > y);
    }

    /**
     * Instances are complete opposites
     *
     * @return
     */
    @Override
    public double getMinValue() {
        return -1;
    }

    /**
     * Instances are the same
     *
     * @return
     */
    @Override
    public double getMaxValue() {
        return 1;
    }
}
