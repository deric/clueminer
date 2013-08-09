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
 * The Pearson correlation coefficient is always between -1 and 1, however
 * Pearson distance we define as non-negative. With 0 meaning that the two
 * series are identical, 1.0 meaning they are completely uncorrelated, and 2.0
 * meaning they are perfect opposites
 *
 * This definition yields a semi-metric: d(a,b) >= 0, and d(a,b) = 0 iff a = b.
 * but the triangular inequality d(a,b) + d(b,c) >= d(a,c) does not hold (e.g.,
 * choose b = a + c).
 *
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class PearsonDistance extends SymmetricDistance {

    private static String name = "Pearson";
    private static float similarityFactor = -1.0f;
    /**
     * FIXME should be 1
     */
    private static int offset = 1;
    /**
     * FIXME should be 0
     */
    private static final long serialVersionUID = -5861415196767414635L;
    private static double TINY = Double.MIN_VALUE;

    @Override
    public String getName() {
        return name;
    }

    /**
     * @deprecated will be removed soon
     * @param matrix
     * @param e1
     * @param e2
     * @return
     */
    public double columns(Matrix matrix, int e1, int e2) {
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

    /**
     * @deprecated will be removed soon
     * @param X
     * @param Y
     * @param g1
     * @param g2
     * @return
     */
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

    /**
     * Classical "centered" Pearson correlation d = 1 - r (value lies between 0
     * and 2)
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double vector(DoubleVector x, DoubleVector y) {
        int n, j;
        double xt, yt;

        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        //number of non-zero elements
        n = 0;
        for (j = 0; j < x.size(); j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                xt = x.get(j);
                yt = y.get(j);

                sumXY += xt * yt;
                sumX += xt;
                sumX2 += xt * xt;
                sumY += yt;
                sumY2 += yt * yt;
                n++;
            }
        }

        double meanX = sumX / n;
        double meanY = sumY / n;
        double centeredSumXY = sumXY - meanY * sumX;
        double centeredSumX2 = sumX2 - meanX * sumX;
        double centeredSumY2 = sumY2 - meanY * sumY;


        return correlation(n, centeredSumXY, centeredSumX2, centeredSumY2);
    }

    protected double correlation(int n, double sumXY, double sumX2, double sumY2) {
        if (n == 0) {
            return Double.NaN;
        }
        // Note that sum of X and sum of Y don't appear here since they are assumed to be 0;
        // the data is assumed to be centered.
        double denominator = Math.sqrt(sumX2) * Math.sqrt(sumY2);
        if (denominator == 0.0) {
            // One or both parties has -all- the same ratings;
            // can't really say much similarity under this measure
            return Double.NaN;
        }

        return 1. - sumXY / denominator;
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
        int n, j;
        double xt, yt;

        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        //number of non-zero elements
        n = 0;
        for (j = 0; j < x.size(); j++) {
            if ((!Double.isNaN(x.value(j))) && (!Double.isNaN(y.value(j)))) {
                xt = x.value(j);
                yt = y.value(j);

                sumXY += xt * yt;
                sumX += xt;
                sumX2 += xt * xt;
                sumY += yt;
                sumY2 += yt * yt;
                n++;
            }
        }

        double meanX = sumX / n;
        double meanY = sumY / n;
        sumXY -= meanY * sumX;
        sumX2 -= meanX * sumX;
        sumY2 -= meanY * sumY;


        return correlation(n, sumXY, sumX2, sumY2);
    }

    @Override
    public double measure(Instance x, Instance y, double[] weights) {
        int n, j;
        double xt, yt;

        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        double w;
        //number of non-zero elements
        n = 0;
        for (j = 0; j < x.size(); j++) {
            if ((!Double.isNaN(x.value(j))) && (!Double.isNaN(y.value(j)))) {
                w = weights[j];
                xt = x.value(j);
                yt = y.value(j);

                sumXY += w * xt * yt;
                sumX += w * xt;
                sumX2 += w * xt * xt;
                sumY += w * yt;
                sumY2 += w * yt * yt;
                n++;
            }
        }

        double meanX = sumX / n;
        double meanY = sumY / n;
        //centering
        // double centeredSumXY = sumXY - meanY * sumX - meanX * sumY + n * meanX * meanY;
        // -> simplified to this
        sumXY -= sumX * meanY;
        sumX2 -= meanX * sumX;
        sumY2 -= meanY * sumY;


        return correlation(n, sumXY, sumX2, sumY2);
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
     * Instances are the same (values are shifted to interval [0, 2] instead of
     * standard [-1, 1])
     *
     * @return
     */
    @Override
    public double getMinValue() {
        return 0.;
    }

    /**
     * Instances are complete opposites (values are shifted to interval [0, 2]
     * instead of standard [-1, 1])
     *
     * @return
     */
    @Override
    public double getMaxValue() {
        return 2.;
    }
}
