package org.clueminer.approximation;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Exponential fitting
 *  y(t) = a * e^(-t) + c
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public final class InvExpApproximator extends Approximator {

    private static String name = "exp-inv";
    private static int numCoeff = 4;
    private double[] params = new double[numCoeff];
    private static String[] paramNames = null;
    private static LMAFunction func = new InvExpFit(1);

    public InvExpApproximator() {
        getParamNames();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void estimate(double[] xAxis, ContinuousInstance dataset, HashMap<String, Double> coefficients) {
        double[][] data = {xAxis, dataset.arrayCopy()};
        params[0] = dataset.getMax();
        params[1] = 0.5;
        params[2] = 0.1;
        params[3] = 0.9;
        //a*e^(-b*t) + c*t

        LMA lma = new LMA(func, params, data);
        lma.fit();
        //  System.out.println("\t\titerations=" + lma.iterationCount);
        //  System.out.println("\t\tchi2=" + lma.chi2);
        int e = 0;
        /* System.out.println("\t\ta =" + lma.parameters[e]);
        System.out.println("\t\tt  =" + (lma.parameters[e + 1]));
        System.out.println("\t\tc =" + lma.parameters[e + 2]);*/

        coefficients.put(paramNames[e], lma.parameters[e]);
        coefficients.put(paramNames[e + 1], (lma.parameters[e + 1]));
        coefficients.put(paramNames[e + 2], lma.parameters[e + 2]);
        coefficients.put(paramNames[e + 3], lma.parameters[e + 3]);
    }

    @Override
    public String[] getParamNames() {
        if (paramNames == null) {
            paramNames = new String[params.length];
            paramNames[0] = getName() + "-a";
            paramNames[1] = getName() + "-t";
            paramNames[2] = getName() + "-c";
            paramNames[3] = getName() + "-d";
        }
        return paramNames;
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
