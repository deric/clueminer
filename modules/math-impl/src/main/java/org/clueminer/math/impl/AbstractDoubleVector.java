package org.clueminer.math.impl;

import org.clueminer.math.DoubleVector;
import org.clueminer.math.Vector;

/**
 * An abstract base class that provides default implementations of common
 * methods in {@link DoubleVector}. {@link DoubleVector} implementations need
 * only implement {@link #size()} and {@link #get(int)} functionality to be
 * read-only vectors.
 */
public abstract class AbstractDoubleVector extends AbstractVector<Double>
        implements DoubleVector, Vector<Double> {

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
     * {@inheritDoc}
     */
    @Override
    public double dot(Vector v) {
        checkForSameSize(v);
        double dot = 0.0;
        for (int i = 0; i < this.size(); i++) {
            dot += this.get(i) * v.get(i);
        }

        return dot;
    }

    /**
     * {@inheritDoc }
     *
     * @param other
     * @return
     */
    @Override
    public Vector<Double> add(Vector<Double> other) {
        checkForSameSize(other);
        int length = other.size();
        Vector<Double> res = duplicate();
        for (int i = 0; i < length; ++i) {
            res.set(i, other.getValue(i) + this.getValue(i));
        }
        return res;
    }

    /**
     * {@inheritDoc }
     *
     * @param other
     * @return
     */
    @Override
    public Vector<Double> minus(Vector<Double> other) {
        checkForSameSize(other);
        int length = other.size();
        Vector<Double> res = duplicate();
        for (int i = 0; i < length; ++i) {
            res.set(i, other.getValue(i) - this.getValue(i));
        }
        return res;
    }

    @Override
    public Vector<Double> times(double scalar) {
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); ++i) {
            res.set(i, this.getValue(i) * scalar);
        }
        return res;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<Double> add(double num) {
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) + num);
        }
        return res;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<Double> minus(double num) {
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) - num);
        }
        return res;
    }

    @Override
    public double pNorm(double p) {
        double norm = 0;
        for (int i = 0; i < size(); i++) {
            norm += Math.pow(Math.abs(get(i)), p);
        }

        return Math.pow(norm, 1.0 / p);
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
