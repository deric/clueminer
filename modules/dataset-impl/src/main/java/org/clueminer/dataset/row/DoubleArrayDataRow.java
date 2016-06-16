/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.dataset.row;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.PlotterFactory;
import org.clueminer.math.Vector;

/**
 * Default row storage for numeric values in dataset. This class wraps a single
 * row of a "spreadsheet".
 *
 * @author Tomas Barton
 */
public class DoubleArrayDataRow extends DataRow<Double> implements Iterable<Double>, Vector<Double>, Instance<Double> {

    private static final long serialVersionUID = -4054619137644952331L;
    /**
     * last is pointing to next empty space
     */
    private int last = 0;
    /**
     * Holds the values for all attributes
     */
    private double[] data;
    private Iterator<Double> it;
    private double unknown = Double.NaN;

    /**
     * Creates a new data row backed by an primitive array.
     *
     * @param data initial data
     */
    public DoubleArrayDataRow(double[] data) {
        super(null);
        set(data);
    }

    public DoubleArrayDataRow(int size) {
        super(null);
        this.data = new double[size];
        //TODO: we should be able to initialize array with zeros. right now
        //it is causing strange problems when standardizing datata
        /* if (size > 0) {
         * last = size - 1; //if we're getting index < size we'll get 0.0 (instead of unknown == Double.NaN)
         * } */
    }

    public DoubleArrayDataRow(int size, Object classValue) {
        super(classValue);
        this.data = new double[size];
        // last = size;
    }

    /**
     * Add value to row and increment counter of last used value
     *
     * @param value
     * @return
     */
    @Override
    public int put(double value) {
        if (last >= data.length) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.618));
        }
        data[last] = value;
        return last++;
    }

    @Override
    public double value(int index) {
        return get(index);
    }

    /**
     * Returns the desired data for the given index.
     *
     * @param index
     * @param defaultValue
     * @return
     */
    @Override
    protected double getValue(int index, double defaultValue) {
        if (index < 0 || index >= last) {
            return defaultValue;
        }
        return value(index);
    }

    @Override
    public Double getValue(int index) {
        return value(index);
    }

    public double item(int index) {
        return data[index];
    }

    /**
     * For values out of array bounds will return unknown value
     *
     * @param index
     * @return
     */
    @Override
    public double get(int index) {
        if (index < 0 || index >= last) {
            return unknown;
        }
        return data[index];
    }

    public Double getDouble(int index) {
        return value(index);
    }

    @Override
    public void set(int index, double value) {
        // if we're trying to reach out of array bounds
        if (index >= data.length) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.618));
        }
        if (index >= last) {
            //last should point to next empty space
            last = index + 1;
        }
        data[index] = value;
    }

    public final void set(double[] data) {
        this.data = data;
        last = data.length;
    }

    @Override
    public void set(int index, Number value) {
        set(index, value.doubleValue());
    }

    @Override
    public void setObject(int index, Object value) {
        set(index, (double) value);
    }


    /**
     * Sets the given data for the given index.
     *
     * @param index
     * @param value
     * @param defaultValue
     */
    @Override
    protected void setValue(int index, double value, double defaultValue) {
        set(index, value);
    }

    /**
     * Number of attributes in the instance
     *
     * @return
     */
    @Override
    public int size() {
        if (data == null) {
            return 0;
        }
        return last;
    }

    /**
     * Does not check allocated capacity
     *
     * @return True when vector does not contain any data
     */
    @Override
    public boolean isEmpty() {
        return (last == 0);
    }

    /**
     * Creates a new array of the given size if necessary and copies the data
     * into the new array.
     *
     * @param numberOfColumns
     */
    @Override
    public void setCapacity(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        double[] newData = new double[numberOfColumns];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    /**
     * Capacity corresponds to currently allocated space for data values.
     *
     * @return
     */
    @Override
    public int getCapacity() {
        return data.length;
    }

    /**
     * Does nothing.
     */
    @Override
    public void trim() {
    }

    /**
     * Remove i-th attribute
     *
     * @param i
     */
    @Override
    public void remove(int i) {
        double[] tmp = data.clone();
        this.data = new double[tmp.length - 1];
        System.arraycopy(tmp, 0, data, 0, i);
        System.arraycopy(tmp, i + 1, data, i, tmp.length - i - 1);
    }

    /**
     * Index of last item in an array
     *
     * @return
     */
    public int getLast() {
        return last;
    }

    @Override
    public Instance copy() {
        DoubleArrayDataRow copy = new DoubleArrayDataRow(this.size());
        for (int i = 0; i < this.size(); i++) {
            copy.set(i, this.value(i));
        }
        return copy;
    }

    /**
     * @TODO consider this: As long as most instance copies provides deep copy,
     * it's not systematic to provide a shallow copy, although it might be
     * useful in some cases.
     *
     * @return
     */
    @Override
    public double[] arrayCopy() {
        return data.clone();
    }

    @Override
    public Plotter getPlotter() {
        //TODO: allow using prefered chart provider
        Collection<Plotter> backend = PlotterFactory.getInstance().filter(DataType.DISCRETE);
        Iterator<Plotter> iter = backend.iterator();
        if (!iter.hasNext()) {
            throw new RuntimeException("could not find plotting backend");
        }
        Plotter plotter = iter.next();
        plotter.addInstance(this);
        return plotter;
    }

    @Override
    public double magnitude() {
        double m = 0;
        int length = size();
        for (int i = 0; i < length; ++i) {
            double d = item(i);
            m += d * d;
        }
        return Math.sqrt(m);
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        checkForSameSize(other);
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getDouble(i) + other.getValue(i));
        }
        return res;
    }

    private void checkForSameSize(Vector<Double> other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Vectors of different sizes cannot be added");
        }
    }

    @Override
    public Vector<Double> minus(Vector<Double> other) {
        checkForSameSize(other);
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getDouble(i) - other.getValue(i));
        }
        return res;
    }

    @Override
    public Vector<Double> times(double scalar) {
        Vector<Double> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getDouble(i) * scalar);
        }
        return res;
    }

    public double add(int index, double delta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double[] toArray() {
        double[] copy = Arrays.copyOf(data, size());
        return copy;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<Double> duplicate() {
        return new DoubleArrayDataRow(this.size());
    }

    class InstanceValueIterator implements Iterator<Double> {

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
    public Iterator<Double> iterator() {
        if (it == null) {
            it = new InstanceValueIterator();
        }
        return it;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        //instances having same values should not be considered as the same
        if (index >= 0) {
            result += (index + prime);
        }
        result += Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoubleArrayDataRow other = (DoubleArrayDataRow) obj;
        if (this.getIndex() != other.getIndex()) {
            return false;
        }
        return Arrays.equals(data, other.data);
    }

    /**
     * Unknown is value for a missing value in a dataset
     *
     * @return by default Double.NaN, but could be changed to some other value
     */
    public double getUnknown() {
        return unknown;
    }

    public void setUnknown(double unknown) {
        this.unknown = unknown;
    }

    /**
     * Returns a string representation of the data row.
     *
     * @return
     */
    @Override
    public String toString() {
        return this.toString(",");
    }

    @Override
    public String toString(String separator) {
        StringBuilder result = new StringBuilder("DoubleArrayData").append("(").append(getCapacity()).append(")[");
        for (int i = 0; i < size(); i++) {
            result.append(i == 0 ? "" : ",").append(data[i]);
        }
        result.append("]");
        return result.toString();
    }
}
