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
package org.clueminer.dataset.row;

import java.util.Arrays;
import java.util.Iterator;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.math.Vector;

/**
 * Implementation of DataRow that is backed by a float array. Please note that
 * for most applications the precision of floats should be high enough. The
 * highest precision is provided by {@link DoubleArrayDataRow}s but these need
 * the double amount compared to these float representations which are therefore
 * a good trade-off between precision and memory usage.
 *
 * @author Tomas Barton
 */
public class FloatArrayDataRow extends DataRow<Float> implements Iterable<Float> {

    private static final long serialVersionUID = -9049887710056073109L;
    private int last = 0;
    /**
     * Holds the data for all attributes.
     */
    private float[] data;

    /**
     * Creates a new data row backed by an primitive array.
     *
     * @param data
     */
    public FloatArrayDataRow(float[] data) {
        super(null);
        set(data);
    }

    /**
     * Create float row with given capacity
     *
     * @param capacity
     */
    public FloatArrayDataRow(int capacity) {
        super(null);
        set(new float[capacity]);
    }

    public FloatArrayDataRow(int capacity, Object classValue) {
        super(classValue);
        set(new float[capacity]);
    }

    @Override
    public String getFullName() {
        return getId() + " - " + getName();
    }

    @Override
    public double value(int index) {
        return data[index];
    }

    /**
     * Returns the desired data for the given index.
     *
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
    public Float getValue(int index) {
        return data[index];
    }

    @Override
    public double get(int index) {
        return data[index];
    }

    /**
     * Add value to row and increment counter of last used value
     *
     * @param value
     * @return
     */
    @Override
    public int put(double value) {
        data[last] = (float) value;
        return last++;
    }

    @Override
    public void set(int index, double value) {
        data[index] = (float) value;
    }

    public final void set(float[] data) {
        this.data = data;
        last = data.length;
    }

    /**
     * Remove i-th attribute
     *
     * @param i
     */
    @Override
    public void remove(int i) {
        float[] tmp = data.clone();
        this.data = new float[tmp.length - 1];
        System.arraycopy(tmp, 0, data, 0, i);
        System.arraycopy(tmp, i + 1, data, i, tmp.length - i - 1);
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected void setValue(int index, double value, double defaultValue) {
        data[index] = (float) value;
    }

    @Override
    public int size() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return (size() == 0);
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
        float[] newData = new float[numberOfColumns];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public int getCapacity() {
        return this.size();
    }

    @Override
    public void trim() {
    }

    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        for (int i = 0; i < size(); i++) {
            res[i] = value(i);
        }
        return res;
    }

    /**
     * there's no better way than deep copy
     *
     * @return
     */
    @Override
    public double[] asArray() {
        return arrayCopy();
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double magnitude() {
        double m = 0;
        int length = size();
        for (int i = 0; i < length; ++i) {
            double d = value(i);
            m += d * d;
        }
        return Math.sqrt(m);
    }

    @Override
    public void set(int index, Number value) {
        set(index, value.floatValue());
    }

    private void checkForSameSize(Vector<Float> other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Vectors of different sizes cannot be added");
        }
    }

    @Override
    public Vector<Float> add(Vector<Float> other) {
        checkForSameSize(other);
        Vector<Float> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) + other.getValue(i));
        }
        return res;
    }

    @Override
    public Vector<Float> duplicate() {
        return new FloatArrayDataRow(this.size());
    }

    @Override
    public Vector<Float> minus(Vector<Float> other) {
        checkForSameSize(other);
        Vector<Float> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) - other.getValue(i));
        }
        return res;
    }

    @Override
    public Vector<Float> times(double scalar) {
        Vector<Float> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) * scalar);
        }
        return res;
    }

    @Override
    public void setObject(int index, Object value) {
        set(index, (float) value);
    }

    class InstanceValueIterator implements Iterator<Float> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Float next() {
            index++;
            return (float) value(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public Iterator<Float> iterator() {
        return new InstanceValueIterator();
    }

    @Override
    public Instance copy() {
        FloatArrayDataRow copy = new FloatArrayDataRow(this.size());
        for (int i = 0; i < this.size(); i++) {
            copy.set(i, this.value(i));
        }
        return copy;
    }

    @Override
    public int hashCode() {
        final int prime = 29;
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
        final FloatArrayDataRow other = (FloatArrayDataRow) obj;
        if (this.getIndex() != other.getIndex()) {
            return false;
        }
        return Arrays.equals(data, other.data);
    }

    /**
     * Returns a string representation of the data row.
     */
    @Override
    public String toString() {
        return this.toString(",");
    }

    @Override
    public String toString(String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(i == 0 ? "" : ",").append(data[i]);
        }
        return result.toString();
    }
}
