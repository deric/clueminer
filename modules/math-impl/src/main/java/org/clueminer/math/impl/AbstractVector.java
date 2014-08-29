package org.clueminer.math.impl;

import org.clueminer.math.Vector;

/**
 * An abstract base class that provides default implementations of common
 * methods in {@link Vector}. {@link Vector} implementations need only implement
 * {@link #size()} and {@link #getDouble(int)} functionality to be read-only
 * vectors.
 */
public abstract class AbstractVector<T extends Number> implements Vector<T> {

    public AbstractVector() {
    }

    /**
     * Throws an {@link UnsupportedOperationException} if called (vector is
     * unmodifiable).
     */
    public double add(int index, double delta) {
        throw new UnsupportedOperationException("Modification is unsupported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector) {
            Vector v = (Vector) o;
            int len = v.size();
            if (len != size()) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                if (!v.getValue(i).equals(getValue(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int len = size();
        int hash = 0;

        for (int i = 0; i < len; ++i) {
            hash ^= i ^ getValue(i).intValue();
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
            double d = getValue(i).doubleValue();
            m += d * d;
        }
        return Math.sqrt(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, Number value) {
        throw new UnsupportedOperationException("Modification is unsupported");
    }

    /**
     * Returns a string description of the full contents of this vector
     */
    @Override
    public String toString() {
        int length = size();
        StringBuilder sb = new StringBuilder(length * 3);
        sb.append('[');
        for (int i = 0; i < length; ++i) {
            sb.append(getValue(i));
            if (i + 1 < length) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}