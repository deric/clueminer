package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Tomas Barton
 */
public class ScalarProduct implements UnivariateFunction {

    private UnivariateFunction f;
    private UnivariateFunction g;
    private UnivariateFunction w;
    private int cnt = 0;

    public ScalarProduct(UnivariateFunction w, UnivariateFunction f, UnivariateFunction g) {
        this.f = f;
        this.g = g;
        this.w = w; //weight
    }

    /**
     * Scalar product of two functions
     *
     * @param x
     * @return
     */
    @Override
    public double value(double x) {
        double value = w.value(x) * f.value(x) * g.value(x);
        //System.out.println("x = " + x + " weight = " + w.value(x) + " cnt = " + (cnt++) + " value = " + value);
        return value;
    }
}
