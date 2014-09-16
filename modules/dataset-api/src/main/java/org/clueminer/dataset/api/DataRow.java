package org.clueminer.dataset.api;

import java.util.Iterator;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public abstract class DataRow<T extends Number> extends AbstractInstance<T> implements Instance<T> {

    private static final long serialVersionUID = 7076642394603104341L;
    protected Instance ancestor;
    protected double[] meta;

    public DataRow(Object classValue) {
        setClassValue(classValue);
    }

    /**
     * Returns the value for the given index.
     *
     * @param index
     * @param defaultValue
     * @return
     */
    protected abstract double getValue(int index, double defaultValue);

    /**
     * Sets the given data for the given index.
     *
     * @param index
     * @param value
     * @param defaultValue
     */
    protected abstract void setValue(int index, double value, double defaultValue);

    /**
     * Trims the number of columns to the actually needed number.
     */
    public abstract void trim();

    @Override
    public double[] getMetaNum() {
        return meta;
    }

    @Override
    public void setMetaNum(double[] meta) {
        this.meta = meta;
    }

    /**
     * Returns a string representation for this data row.
     *
     * @return
     */
    @Override
    public abstract String toString();

    /**
     * Returns the value stored at the given {@link Attribute}'s index. Returns
     * Double.NaN if the given attribute is null.
     *
     * @param attribute
     * @return
     */
    public double getValue(Attribute attribute) {
        if (attribute == null) {
            return Double.NaN;
        } else {
            try {
                return this.value(attribute.getIndex());
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("DataRow: of Attribute " + attribute.getName() + " is out of bounds.");
            }
        }
    }

    /**
     * Sets the value of the {@link Attribute} to <code>value</code>.
     *
     * @param attribute
     * @param value
     */
    public void setValue(Attribute attribute, double value) {
        set(attribute.getIndex(), value);
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
    public double pNorm(double p) {
        double norm = 0;
        for (int i = 0; i < size(); i++) {
            norm += Math.pow(Math.abs(get(i)), p);
        }

        return Math.pow(norm, 1.0 / p);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<T> add(double num) {
        Vector<T> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) + num);
        }
        return res;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<T> subtract(double num) {
        Vector<T> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) - num);
        }
        return res;
    }

    /**
     * When preprocessing data sometimes we need to display reference to
     * original data
     *
     * @return Instance from which was this one derived
     */
    @Override
    public Instance getAncestor() {
        return ancestor;
    }

    /**
     * Set reference to original data row
     *
     * @param instance
     */
    @Override
    public void setAncestor(Instance instance) {
        this.ancestor = instance;
    }

    @Override
    public Iterator<? extends Object> values() {
        return new InstanceValueIterator();
    }

    private class InstanceValueIterator implements Iterator<Double> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Double next() {
            index++;
            return value(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public String[] toStringArray() {
        String[] res = new String[size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = String.valueOf(getValue(i, Double.NaN));
        }
        return res;
    }
}
