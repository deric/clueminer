package org.clueminer.approximation;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;

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
