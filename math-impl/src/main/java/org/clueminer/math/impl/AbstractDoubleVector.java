package org.clueminer.math.impl;

import org.clueminer.math.DoubleVector;

/**
 * An abstract base class that provides default implementations of common
 * methods in {@link DoubleVector}. {@link DoubleVector} implementations need
 * only implement {@link #size()} and {@link #get(int)} functionality to be
 * read-only vectors.
 */
public abstract class AbstractDoubleVector extends AbstractVector<Double>
        implements DoubleVector {

    public AbstractDoubleVector() {
    }

    /**
     * Throws an {@link UnsupportedOperationException} if called (vector is
     * unmodifiable).
     */
    @Override
    public double add(int index, double delta) {
        throw new UnsupportedOperationException("set is not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DoubleVector) {
            DoubleVector v = (DoubleVector) o;
            int len = v.size();
            if (len != size()) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                if (v.get(i) != get(i)) {
                    return false;
                }
            }
            return true;
        } else {
            return super.equals(o);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(int index) {
        return get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int len = size();
        int hash = 0;
        for (int i = 0; i < len; ++i) {
            hash ^= i ^ (int) (get(i));
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double magnitude() {
        double m = 0;
        int length = size();
        for (int i = 0; i < length; ++i) {
            double d = get(i);
            m += d * d;
        }
        return Math.sqrt(m);
    }

    /**
     * Throws an {@link UnsupportedOperationException} if called (vector is
     * unmodifiable).
     */
    @Override
    public void set(int index, double value) {
        throw new UnsupportedOperationException("set is not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, Number value) {
        set(index, value.doubleValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] toArray() {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = get(i);
        }
        return arr;
    }
}