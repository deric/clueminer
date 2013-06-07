package org.clueminer.approximation;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * @see http://mathworld.wolfram.com/LegendrePolynomial.html
 * @author Tomas Barton
 */
public class LegendrePolynomial implements UnivariateFunction {
    
    public LegendrePolynomial(int n){
        
    }
    
    private void generate(int n){
        int l = n;
        while (l > 0){
            
            l -=2;
        }
    }

    @Override
    public double value(double x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
