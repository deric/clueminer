/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.math.impl;

import java.io.Serializable;
import java.util.Arrays;
import org.clueminer.math.DoubleEntry;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.SparseDoubleVector;
import org.clueminer.math.Vector;

/**
 * A {@code Vector} where all values are held in memory. The underlying
 * implementation is simply an array of doubles.
 * <p>
 *
 * @author Keith Stevens
 * @author Tomas Barton
 */
public class DenseVector extends AbstractDoubleVector implements Serializable {

    private static final long serialVersionUID = 8381939903992766044L;
    /**
     * The values of this {@code DenseVector}.
     */
    private final double[] vector;
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
     * @param v The {@code Vector} to copy from.
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
     *
     * @return
     */
    @Override
    public double add(int index, double delta) {
        magnitude = -1;
        vector[index] += delta;
        return vector[index];
    }

    /**
     * {@inheritDoc}
     *
     * @param index
     * @return
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
     *
     * @return
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
     *
     * @param index
     * @param value
     */
    @Override
    public void set(int index, double value) {
        magnitude = -1;
        vector[index] = value;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public double[] toArray() {
        return Arrays.copyOf(vector, vector.length);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int size() {
        return vector.length;
    }


    @Override
    public Vector<Double> duplicate() {
        return new DenseVector(this.size());
    }

}
