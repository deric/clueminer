package org.clueminer.distance.api;

import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractDistance implements Distance {

    private static final long serialVersionUID = -4166447737887574607L;

    @Override
    public abstract String getName();

    /**
     * Return TRUE if x is better than y
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean compare(double x, double y) {
        return x < y;
    }

    /**
     * Minimal value of metric
     *
     * @return
     */
    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return Double.POSITIVE_INFINITY;
    }


    protected void checkInput(Vector<Double> x, Vector<Double> y) {
        if (x.size() != y.size()) {
            throw new ArithmeticException("Both instances should contain the same number of values! x size: " + x.size() + " != y size: " + y.size());
        }
    }

    protected void checkInput(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new ArithmeticException("Both instances should contain the same number of values! x size: " + x.length + " != y size: " + y.length);
        }
    }
}
