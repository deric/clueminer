package org.clueminer.approximation;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.fit.FivePLFit;
import org.openide.util.lookup.ServiceProvider;

/**
 * Computes average from all items
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public final class FivePLApproximator extends Approximator {

    private static final String name = "5PL";
    private static final String[] paramNames = {"a", "b", "c", "d", "g"};
    private static final int numCoeff = 5;
    private double[] params = new double[numCoeff];
    private static final LMAFunction func = new FivePLFit();

    public FivePLApproximator() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void estimate(double[] xAxis, ContinuousInstance instance, HashMap<String, Double> coefficients) {
        double[][] data = {xAxis, instance.arrayCopy()};
        for (int i = 0; i < numCoeff; i++) {
            params[i] = Math.random();
        }

        LMA lma = new LMA(func, params, data);
        lma.fit();
        for (int i = 0; i < numCoeff; i++) {
            coefficients.put(paramNames[i], lma.parameters[i]);
        }
    }

    @Override
    public String[] getParamNames() {
        return paramNames;
    }

    /**
     * @param x
     * @param coeff
     * @return
     */
    @Override
    public double getFunctionValue(double x, double[] coeff) {
        return func.getY(x, coeff);
    }

    @Override
    public int getNumCoefficients() {
        return numCoeff;
    }
}
