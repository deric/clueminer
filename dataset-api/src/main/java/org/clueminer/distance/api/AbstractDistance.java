package org.clueminer.distance.api;

import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractDistance implements DistanceMeasure {

    private static final long serialVersionUID = -4166447737887574607L;

    @Override
    public abstract String getName();

    @Override
    public abstract float getSimilarityFactor();

    /**
     * In fact this should be multiplied by tree min height
     *
     * @return
     */
    @Override
    public abstract int getNodeOffset();

    @Override
    public boolean useTreeHeight() {
        return false;
    }

    /**
     * Return TRUE if x is better than y
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean compare(double x, double y) {
        return x < y;
    }

    /**
     * Minimal value of metric
     *
     * @return
     */
    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public abstract double rows(Matrix a, Matrix b, int i, int j);

    @Override
    public abstract double columns(Matrix a, int i, int j);

    @Override
    public double rows(Matrix matrix, int e1, int e2, float factor) {
        return rows(matrix, e1, e2) * factor;
    }

    @Override
    public double rows(Matrix matrix, int e1, int e2) {
        return rows(matrix, matrix, e1, e2);
    }

    @Override
    public double rows(Matrix A, Matrix B, int e1, int e2, float factor) {
        return rows(A, B, e1, e2) * factor;
    }

    @Override
    public double columns(Matrix matrix, int e1, int e2, float factor) {
        return columns(matrix, e1, e2) * factor;
    }

    protected void checkInput(Instance x, Instance y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("Both instances should contain the same number of values! x size: " + x.size() + " != y size: " + y.size());
        }
    }
}
