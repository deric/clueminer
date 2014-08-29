package org.clueminer.math.impl;

import org.clueminer.math.DoubleVector;
import org.clueminer.math.Vector;

/**
 * A decorator for {@link DoubleVector}s that scales every value in a given
 * {@link DoubleVector} by some non zero scale.
 *
 * </p>
 *
 * Note that this automatically computes the scaling of a {@link
 * ScaledDoubleVector} so that backing vector is scaled only once, thus
 * preventing any recursive calls to scaling.
 *
 * @author Keith Stevens
 */
public class ScaledDoubleVector implements DoubleVector {

    /**
     * The original vector.
     */
    private DoubleVector vector;
    /**
     * The scale applied to each value in {@code vector}
     */
    private double scale;

    /**
     * Creates a new {@link ScaledDoubleVector} that decorates a given {@link
     * DoubleVector} by scaling each value in {@code vector} by {@code scale}.
     */
    public ScaledDoubleVector(DoubleVector vector, double scale) {
        if (scale == 0d) {
            throw new IllegalArgumentException("Cannot scale a vector by 0");
        }

        // If the vector we are to orthonormalize is already scaled, get its
        // backing data and create a new instance that is rescaled by the
        // product of both scalars.  This avoids unnecessary recursion to
        // multiply all the values together for heavily scaled vectors.
        if (vector instanceof ScaledDoubleVector) {
            ScaledDoubleVector sdv = (ScaledDoubleVector) vector;
            this.vector = sdv.vector;
            this.scale = scale * sdv.scale;
        } else {
            this.vector = vector;
            this.scale = scale;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double add(int index, double delta) {
        return vector.add(index, delta / scale) * scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double get(int index) {
        return vector.get(index) * scale;
    }

    @Override
    public Double getValue(int index) {
        return get(index);
    }

    /**
     * Returns the vector whose values are scaled by this instance
     */
    public DoubleVector getBackingVector() {
        return vector;
    }

    /**
     * Returns the scalar multiple used by this instance to change the values of
     * the backing vector
     */
    public double getScalar() {
        return scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, double value) {
        vector.set(index, value / scale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, Number value) {
        vector.set(index, value.doubleValue() / scale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double magnitude() {
        double magnitude = 0;
        for (int c = 0; c < size(); ++c) {
            magnitude += get(c);
        }
        return Math.sqrt(magnitude);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double dot(Vector v) {
        if (this.size() != v.size()) {
            throw new ArithmeticException("Vectors must have the same length" + this.size() + " != " + v.size());
        }
        double dot = 0.0;
        for (int i = 0; i < this.size(); i++) {
            dot += this.get(i) * v.get(i);
        }

        return dot;
    }

    @Override
    public int size() {
        return vector.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] toArray() {
        double[] values = vector.toArray();
        for (int i = 0; i < values.length; ++i) {
            values[i] *= scale;
        }
        return values;
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        double v;
        int length = other.size();
        for (int i = 0; i < length; ++i) {
            v = other.getValue(i).doubleValue() + this.get(i);
            this.set(i, v);
        }
        return this;
    }

    @Override
    public double pNorm(double p) {
        double norm = 0;
        for (int i = 0; i < size(); i++) {
            norm += Math.pow(Math.abs(get(i)), p);
        }

        return Math.pow(norm, 1.0 / p);
    }
}
