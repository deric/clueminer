/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public class LegendreApproximator extends Approximator {

    private static String name = "legendre";
    private int maxDegree;
    private String[] names = null;
    private double[] params;
    private LegendrePolynomial polynomials[];

    public LegendreApproximator() {
        setMaxDegree(5);
    }

    public LegendreApproximator(int degree) {
        setMaxDegree(degree);
    }

    /**
     * Set maximum degree which will be analyzed
     *
     * @param degree
     */
    public final void setMaxDegree(int degree) {
        this.maxDegree = degree;
        polynomials = new LegendrePolynomial[maxDegree];
        //zero degree is just a constant function
        //don't be a racist, constant function has right to live!
        for (int i = 0; i < maxDegree; i++) {
            polynomials[i] = new LegendrePolynomial(i);
        }
        params = new double[maxDegree];
        names = null;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Orthogonal polynomials have very useful properties in this case we try to
     * use product of data function and a orthogonal polynomial to describe
     * properties of data.
     *
     * @param xAxis
     * @param instance
     * @param coefficients
     */
    @Override
    public void estimate(double[] xAxis, ContinuousInstance instance, HashMap<String, Double> coefficients) {
        params = new double[maxDegree];
        // sum product of two functions
        for (int j = 0; j < maxDegree; j++) {
            for (int i = 0; i < xAxis.length; i++) {
                params[j] += polynomials[j].value(xAxis[i]) * instance.value(i);
            }
        }

        for (int i = 0; i < params.length; i++) {
            coefficients.put(names[i], params[i]);
        }
    }

    @Override
    public String[] getParamNames() {
        if (names == null) {
            names = new String[params.length];
            for (int i = 0; i < polynomials.length; i++) {
                names[i] = getName() + "-" + i; //we don't skip polynomial of 0 degree
            }
        }
        return names;
    }

    @Override
    public double getFunctionValue(double x, double[] coeff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    @Override
    public int getNumCoefficients() {
        return getMaxDegree();
    }
}
