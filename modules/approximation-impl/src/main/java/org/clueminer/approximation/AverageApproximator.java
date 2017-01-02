/*
 * Copyright (C) 2011-2017 clueminer.org
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
 * Computes average from all items
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public final class AverageApproximator extends Approximator {

    private static String name = "avg";
    private static String[] paramNames = null;
    private static int numCoeff = 3;

    public AverageApproximator() {
        getParamNames();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void estimate(double[] xAxis, ContinuousInstance instance, HashMap<String, Double> coefficients) {
        double total = 0;
        for (int i = 0; i < instance.size(); i++) {
            total += instance.value(i);
        }
        double res = total / instance.size();
        coefficients.put(getName(), res);
        coefficients.put("min", instance.getMin());
        coefficients.put("max", instance.getMax());
    }

    @Override
    public String[] getParamNames() {
        if (paramNames == null) {
            paramNames = new String[numCoeff];
            paramNames[0] = name;
            paramNames[1] = "min";
            paramNames[2] = "max";
        }
        return paramNames;
    }

    /**
     * We dont really expect precise interpolation in here, so just
     * the averate value is used
     *
     * @param x
     * @param coeff
     * @return
     */
    @Override
    public double getFunctionValue(double x, double[] coeff) {
        return coeff[0];
    }

    @Override
    public int getNumCoefficients() {
        return numCoeff;
    }
}
