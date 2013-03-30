package org.clueminer.approximation;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import jaolho.data.lma.implementations.PolynomialFit;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public final class PolynomialApproximator6 extends Approximator {

    public static String name = "poly6";
    private static String[] names = null;
    double[] params = new double[7]; //+1 constant
    private static LMAFunction func = new PolynomialFit();
    
    public PolynomialApproximator6(){
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

 /*       System.out.println(getName());
        System.out.println("\t\titerations=" + lma.iterationCount);
        System.out.println("\t\tchi2=" + lma.chi2);*/
        int e = 0;
        for(int i=0; i < params.length; i++){
           // System.out.println("\t\tx"+i+" =" + lma.parameters[e+i]);
            coefficients.put(getName() + "-"+i, lma.parameters[e+i]);
        }
    }

    @Override
    public String[] getParamNames() {
        if(names == null){
            names = new String[params.length];
            for(int i = 0; i < params.length; i++){
                names[i] = getName() + "-"+i;
            }
        }
        return names;
    }
    
    @Override
    public double getFunctionValue(double x, double[] coeff){
        return func.getY(x, coeff);
    }
}
