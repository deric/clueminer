package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.Matrix;

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
    public double measure(Instance x, Instance y) {
        if (x.size() != y.size()) {
            throw new RuntimeException("x size: " + x.size() + " != y size: " + y.size());
        }
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.pow(Math.abs(y.value(i) - x.value(i)), power);
        }

        return Math.pow(sum, 1 / power);
    }

    @Override
    public double vector(DoubleVector x, DoubleVector y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("Both instances should contain the same "
                    + "number of values. x= " + x.size() + " != " + y.size());
        }
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.pow(Math.abs(y.get(i) - x.get(i)), power);
        }

        return Math.pow(sum, 1 / power);
    }

    @Override
    public float getSimilarityFactor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNodeOffset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double rows(Matrix a, Matrix b, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double columns(Matrix a, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
