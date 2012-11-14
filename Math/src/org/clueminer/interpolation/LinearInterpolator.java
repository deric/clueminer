package org.clueminer.interpolation;

import org.clueminer.math.Numeric;


/**
 *
 * @author Tomas Barton
 */
public class LinearInterpolator extends Interpolator {

    @Override
    public double getValue(Numeric[] axisX, Numeric[] axisY, double x, int lower, int upper) {
        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if(upper >= axisY.length){
            upper = axisY.length-1;
        }
        double res= axisY[lower].getValue() + (axisY[upper].getValue() - axisY[lower].getValue())
                * (x - axisX[lower].getValue()) / (axisX[upper].getValue() - axisX[lower].getValue());
        //System.out.println("x= "+x+" lower= "+lower+" upper= "+upper+", ax.l="+axisX.length+", ay.l="+axisY.length+" r= "+res);
        return res;
    }
    

    @Override
    public double getValue(double[] axisX, double[] axisY, double x, int lower, int upper) {
        /**
         * y = y_a + (y_b - y_a) * (x - x_a) / (x_b - x_a)
         */
        if(upper >= axisY.length){
            upper = axisY.length-1;
        }
        double res= axisY[lower] + (axisY[upper] - axisY[lower]) * (x - axisX[lower]) / (axisX[upper] - axisX[lower]);
        System.out.println("x= "+x+" lower= "+lower+" upper= "+upper+", ax.l="+axisX.length+", ay.l="+axisY.length+" r= "+res);
        return res;
    }
}
