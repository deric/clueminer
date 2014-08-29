package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * @see http://mathworld.wolfram.com/OrthogonalPolynomials.html
 * @author Tomas Barton
 */
public class ChebyshevWeightingFactor implements UnivariateFunction {
    
    public ChebyshevWeightingFactor() {
    }

    /**
     *
     *
     * @param x
     * @return {latex}(1-x^2)^(-1/2) {/latex}
     */
    @Override
    public double value(double x) {
        double val = FastMath.pow((1.0 - FastMath.pow(x, 2)), -0.5);
        if(Double.isNaN(val)){
            return 0.0;
        }
        return val;
    }
}
