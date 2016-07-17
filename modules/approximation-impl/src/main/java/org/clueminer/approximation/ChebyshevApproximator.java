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

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.fit.ChebyshevFit;

/**
 *
 * @author Tomas Barton
 */
public class ChebyshevApproximator extends Approximator {

    protected static String name = "chebyshev";
    protected static String[] names = null;
    protected double[] params;
    protected static LMAFunction func = new ChebyshevFit();
    protected int numCoeff;

    public ChebyshevApproximator(int degree) {
        numCoeff = degree + 1;
        params = new double[numCoeff]; //+1 constant
        getParamNames();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void estimate(double[] xAxis, ContinuousInstance dataset, HashMap<String, Double> coefficients) {
        double[][] data = {xAxis, dataset.arrayCopy()};

        LMA lma = new LMA(func, params, data);
        lma.fit();

        int e = 0;
        for (int i = 0; i < params.length; i++) {
            coefficients.put(getName() + "-" + i, lma.parameters[e + i]);
        }
    }

    @Override
    public final String[] getParamNames() {
        if (names == null) {
            names = new String[params.length];
            for (int i = 0; i < params.length; i++) {
                names[i] = getName() + "-" + i;
            }
        }
        return names;
    }

    @Override
    public double getFunctionValue(double x, double[] coeff) {
        return func.getY(x, coeff);
    }

    @Override
    public int getNumCoefficients() {
        return numCoeff;
    }
}
