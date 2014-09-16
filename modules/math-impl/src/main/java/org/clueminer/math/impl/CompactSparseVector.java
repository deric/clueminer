package org.clueminer.math.impl;

import java.io.Serializable;
import java.util.Iterator;
import org.clueminer.math.DoubleEntry;
import org.clueminer.math.SparseDoubleVector;
import org.clueminer.math.Vector;

/**
 * A {@code Vector} instance that keeps only the non-zero values in memory,
 * thereby saving space at the expense of time.
 *
 * <p> See {@link SparseDoubleArray} for details on how the sparse
 * representation is implemented.
 *
 * @author Keith Stevens
 */
public class CompactSparseVector extends AbstractDoubleVector
        implements SparseDoubleVector, Serializable, Iterable<DoubleEntry>, Vector<Double> {

    private static final long serialVersionUID = 1L;
    /**
     * The {@code SparseDoubleArray} which provides most of the functionality in
     * this class.
     */
    private SparseDoubleArray vector;
    /**
     * The magnitude of the vector or -1 if the value is currently invalid and
     * needs to be recomputed.
     */
    private double magnitude;

    /**
     * Creates a {@code CompactSparseVector} that grows to the maximum size set
     * by {@link Double#MAX_VALUE}.
     */
    public CompactSparseVector() {
        vector = new SparseDoubleArray();
        magnitude = 0;
    }

    /**
     * Create a {@code CompactSparseVector} with the given size, having no
     * non-zero values.
     *
     * @param length The length of this {@code CompactSparseVector}.
     */
    public CompactSparseVector(int length) {
        vector = new SparseDoubleArray(length);
        magnitude = 0;
    }

    /**
     * Create a {@code CompactSparseVector} from an array, saving only the non
     * zero entries.
     *
     * @param array The double array to produce a sparse vector from.
     */
    public CompactSparseVector(double[] array) {
        vector = new SparseDoubleArray(array);
        magnitude = -1;
    }

    /**
     * Create a {@code CompactSparseVector} from an array, saving only the non
     * zero entries.
     *
     * @param v The double array to produce a sparse vector from.
     */
    public CompactSparseVector(SparseDoubleVector v) {
        int length = v.size();
        int[] nz = v.getNonZeroIndices();
        double[] values = new double[nz.length];
        for (int i = 0; i < nz.length; ++i) {
            values[i] = v.get(nz[i]);
        }
        vector = new SparseDoubleArray(nz, values, length);
        magnitude = -1;
    }

    /**
     * Create a {@code CompactSparseVector} using the indices and their
     * respecitve values.
     *
     * @param nonZeroIndices an sorted array of positive values representing the
     *                       non-zero indices of the array
     * @param values an array of values that correspond their respective indices
     * @param length the total length of the array
     *
     * @throw IllegalArgumentException if {@code indices} and {@code values}
     * have different lengths or if {@code indices} contains duplicate elements
     * or those not in sorted order
     */
    public CompactSparseVector(int[] nonZeroIndices, double[] values,
            int length) {
        vector = new SparseDoubleArray(nonZeroIndices, values, length);
        magnitude = -1;
    }

    /**
     * {@inheritDoc}
     */
    public double add(int index, double delta) {
        magnitude = -1;
        return vector.addPrimitive(index, delta);
    }

    /**
     * Returns an iterator over all the non-zero indices and values in this
     * vector.
     */
    @Override
    public Iterator<DoubleEntry> iterator() {
        return vector.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double get(int index) {
        return vector.getPrimitive(index);
    }

    @Override
    public Double getValue(int index) {
        return get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getNonZeroIndices() {
        return vector.getElementIndices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double magnitude() {
        // Check whether the current magnitude is valid and if not, recompute it
        if (magnitude < 0) {
            double m = 0;
            for (DoubleEntry e : this) {
                m += e.value() * e.value();
            }
            magnitude = Math.sqrt(m);
        }
        return magnitude;
    }

    /**
     * {@inheritDoc}
     * @param values
     */
    public void set(double[] values) {
        vector = new SparseDoubleArray(values);
        magnitude = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(int index, double value) {
        vector.setPrimitive(index, value);
        magnitude = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] toArray() {
        double[] array = new double[vector.length()];
        return vector.toPrimitiveArray(array);
    }

    @Override
    public int size() {
        return vector.length();
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        double v;
        int length = other.size();
        for (int i = 0; i < length; ++i) {
            v = other.getValue(i) + this.getValue(i);
            this.set(i, v);
        }
        return this;
    }

    @Override
    public Vector<Double> duplicate() {
        return new CompactSparseVector(this.size());
    }
}
