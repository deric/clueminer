/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Orthogonal polynomials which have very useful properties in the solution of
 * mathematical and physical problems
 *
 * @see http://mathworld.wolfram.com/LegendrePolynomial.html
 * @author Tomas Barton
 */
public class LegendrePolynomial implements UnivariateFunction {

    double[] coeff;
    private final int n;

    public LegendrePolynomial(int n) {
        generate(n);
        this.n = n;
    }

    /**
     * @see http://en.wikipedia.org/wiki/Legendre_polynomials for implementation
     *
     * using 2^n * \sum_{k=0}^n x^k {n \choose k}{\frac{n+k-1}2\choose n}
     *
     * @param n
     */
    private void generate(int n) {
        coeff = new double[n + 1]; //n degrees + constant
        double twoN = FastMath.pow(2, n);
        double binomDivi;
        for (int k = 0; k <= n; k++) {
            binomDivi = (n + k - 1) / 2.0;
            //requires binomial coefficient which accepts double as argument
            coeff[k] += CombinatoricsUtils.binomialCoefficient(n, k) * binomial(binomDivi, n);
        }

        //multiply by 2^n
        for (int i = 0; i < coeff.length; i++) {
            coeff[i] *= twoN;
        }
    }

    @Override
    public double value(double x) {
        double value = 0.0;
        for (int i = 0; i < coeff.length; i++) {
            if (coeff[i] != 0) {
                if (i > 0) {
                    value += coeff[i] * Math.pow(x, i);
                } else {
                    value += coeff[i];
                }
            }
        }
        return value;
    }

    /**
     * Binomial coefficient for real numbers - the number of ways of picking y
     * unordered outcomes from x possibilities
     *
     * @see http://mathworld.wolfram.com/BinomialCoefficient.html
     *
     * @param x
     * @param y
     * @return binomial coefficient to be generalized to noninteger arguments
     */
    public double binomial(double x, double y) {
        double res = Gamma.gamma(x + 1) / (Gamma.gamma(y + 1) * Gamma.gamma(x - y + 1));
        if (Double.isNaN(res)) {
            return 0.0;
        }
        return res;
    }

    protected double[] getCoefficients() {
        return coeff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i = coeff.length - 1; i >= 0; i--) {
            if (coeff[i] != 0) {
                if (j > 0 && coeff[i] > 0) {
                    sb.append(" + ");
                }
                j++; //how many non-null coefficients we have
                sb.append(coeff[i]);
                if (i > 0) {
                    sb.append("*x^").append(i);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Factor times Kronecker Delta should be either 0 when Legendre polynomial
     * have different degree or equal to 1.0 when have the same degree
     *
     * \int_{-1}^{1} p_m(x) * p_n(x) dx = delta_{mn} * constFactor(n)
     *
     * @see http://mathworld.wolfram.com/KroneckerDelta.html
     * @return
     */
    public double constFactor() {
        return 2.0 / (2 * n + 1);
    }

    /**
     * Return number of polynomial coefficients
     *
     * @return
     */
    public int numParams() {
        return coeff.length;
    }
}
