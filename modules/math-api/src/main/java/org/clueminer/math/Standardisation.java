package org.clueminer.math;

/**
 *
 * @author Tomas Barton
 */
public abstract class Standardisation {
    
    public abstract String getName();
    
    public abstract double[][] optimize(double[][] a, int m, int n);    
    
    public double average(double[] a){
        double total = 0.0;
        for(int i=0; i < a.length; i++){
            total += a[i];
        }
        return (total / a.length);
    }
    
}
