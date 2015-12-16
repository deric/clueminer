package org.clueminer.distance;

import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public class MinkowskiDistance extends SymmetricDistance {

    private static final long serialVersionUID = 1115253168643019620L;
    protected double power;

    @Override
    public String getName() {
        return "Minkowski distance, power= " + power;
    }

    /**
     * The power of 2 gives Euclidean distance
     */
    public MinkowskiDistance() {
        this(2);
    }

    public MinkowskiDistance(double power) {
        this.power = power;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.pow(Math.abs(y.get(i) - x.get(i)), power);
        }

        return Math.pow(sum, 1 / power);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            if ((!Double.isNaN(x.get(i))) && (!Double.isNaN(y.get(i)))) {
                sum += Math.pow(Math.abs(weights[i] * y.get(i) - weights[i] * x.get(i)), power);
            }
        }

        return Math.pow(sum, 1 / power);
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
