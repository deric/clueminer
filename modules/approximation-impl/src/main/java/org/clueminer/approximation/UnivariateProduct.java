package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Tomas Barton
 */
public class UnivariateProduct implements UnivariateFunction {

    private UnivariateFunction f1;
    private UnivariateFunction f2;

    public UnivariateProduct(UnivariateFunction f1, UnivariateFunction f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    /**
     * Return product of two functions at given x
     *
     * @param x
     * @return
     */
    @Override
    public double value(double x) {
        return f1.value(x) * f2.value(x);
    }
}
