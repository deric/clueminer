package org.clueminer.distance;

import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Vector;
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
@ServiceProvider(service = Distance.class)
public class PearsonDistance extends SymmetricDistance {

    private static final String NAME = "Pearson";
    private static final long serialVersionUID = -5861415196767414635L;

    @Override
    public String getName() {
        return NAME;
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

    /**
     * Classical "centered" Pearson correlation d = 1 - r (value lies between 0
     * and 2)
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
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
        sumXY -= meanY * sumX;
        sumX2 -= meanX * sumX;
        sumY2 -= meanY * sumY;

        return correlation(n, sumXY, sumX2, sumY2);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
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
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                w = weights[j];
                xt = x.get(j);
                yt = y.get(j);

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

    @Override
    public boolean isSubadditive() {
        return false;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
