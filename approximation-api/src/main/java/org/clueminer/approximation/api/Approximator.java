package org.clueminer.approximation.api;

import java.util.HashMap;
import org.clueminer.dataset.api.ContinuousInstance;


/**
 * Compute characteristic number(s) of given dataset
 * 
 * @author Tomas Barton
 */
public abstract class Approximator {

    public abstract String getName();

    public abstract void estimate(double[] xAxis, ContinuousInstance instance, HashMap<String, Double> coefficients);
    
    /**
     * Names of all attributes provided by this approximator
     * @return array of Strings (names)
     */
    public abstract String[] getParamNames();

    public abstract double getFunctionValue(double x, double[] coeff);

    public double[] parseParams(HashMap<String, Double> coefficients) {
        String[] names = getParamNames();
        double[] res = new double[names.length];
        for (int i = 0; i < names.length; i++) {
            res[i] = coefficients.get(names[i]);
        }
        return res;
    }
}
