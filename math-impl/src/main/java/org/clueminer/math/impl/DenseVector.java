package org.clueminer.math.impl;

import java.io.Serializable;
import java.util.Arrays;
import org.clueminer.math.DoubleEntry;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.SparseDoubleVector;
import org.clueminer.math.Vector;

/**
 * A {@code Vector} where all values are held in memory. The underlying
 * implementation is simply an array of doubles. <p>
 *
 * @author Keith Stevens
 */
public class DenseVector extends AbstractDoubleVector implements Serializable {

    private static final long serialVersionUID = 8381939903992766044L;
    /**
     * The values of this {@code DenseVector}.
     */
    private double[] vector;
    /**
     * The magnitude of the vector or -1 if the value is currently invalid and
     * needs to be recomputed
     */
    private double magnitude;

    /**
     * Create an {@code DenseVector} with all values starting at 0 with the
     * given length.
     *
     * @param vectorLength The size of the vector to create.
     */
    public DenseVector(int vectorLength) {
        vector = new double[vectorLength];
        magnitude = 0;
    }

    /**
     * Create a {@code DenseVector} taking the values given by {@code vector}.
     * The created vector contains no references to the provided array, so
     * changes to either will not be reflected in the other.
     *
     * @param vector The vector values to start with.
     */
    public DenseVector(double[] vector) {
        this.vector = Arrays.copyOf(vector, vector.length);
        magnitude = -1;
    }

    /**
     * Create a {@code DenseVector} by copying the values from another {@code
     * Vector}.
     *
     * @param vector The {@code Vector} to copy from.
     */
    @SuppressWarnings("unchecked")
    public DenseVector(DoubleVector v) {
        this.vector = new double[v.size()];
        magnitude = v.magnitude();
        if (v instanceof Iterable) {
            for (DoubleEntry e : ((Iterable<DoubleEntry>) v)) {
                vector[e.index()] = e.value();
            }
        } else if (v instanceof SparseDoubleVector) {
            for (int i : ((SparseDoubleVector) v).getNonZeroIndices()) {
                vector[i] = v.get(i);
            }
        } else {
            for (int i = 0; i < v.size(); ++i) {
                vector[i] = v.get(i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double add(int index, double delta) {
        magnitude = -1;
        vector[index] += delta;
        return vector[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double get(int index) {
        return vector[index];
    }

    @Override
    public Double getValue(int index) {
        return get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double magnitude() {
        if (magnitude < 0) {
            double m = 0;
            for (double d : vector) {
                m += d * d;
            }
            magnitude = Math.sqrt(m);
        }
        return magnitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, double value) {
        magnitude = -1;
        vector[index] = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] toArray() {
        return Arrays.copyOf(vector, vector.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return vector.length;
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        double v;
        int length = other.size();
        for (int i = 0; i < length; ++i) {
            v = other.getValue(i).doubleValue() + this.getValue(i).doubleValue();
            this.set(i, v);
        }
        return this;
    }
}
