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

import java.util.Iterator;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Vector;

/**
 *
 * @author deric
 */
public class LongDataRow extends DataRow<Long> implements Iterable<Long>, Vector<Long>, Instance<Long> {

    /**
     * last is pointing to next empty space
     */
    private int last = 0;
    /**
     * Holds the values for all attributes
     */
    private long[] data;
    private Iterator<Double> it;
    private double unknown = Double.NaN;

    /**
     * Creates a new data row backed by an primitive array.
     *
     * @param data initial data
     */
    public LongDataRow(long[] data) {
        super(null);
        set(data);
    }

    public LongDataRow(int size) {
        super(null);
        this.data = new long[size];
        //TODO: we should be able to initialize array with zeros. right now
        //it is causing strange problems when standardizing datata
        /* if (size > 0) {
         * last = size - 1; //if we're getting index < size we'll get 0.0 (instead of unknown == Double.NaN)
         * } */
    }

    public LongDataRow(int size, Object classValue) {
        super(classValue);
        this.data = new long[size];
        // last = size;
    }

    @Override
    protected double getValue(int index, double defaultValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setValue(int index, double value, double defaultValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void trim() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int put(double value) {
        if (last >= data.length) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.618));
        }
        data[last] = (long) value;
        return last++;
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double value(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        data[index] = (long) value;
    }

    @Override
    public void setObject(int index, Object value) {
        set(index, (long) value);
    }

    public final void set(long[] data) {
        this.data = data;
        last = data.length;
    }

    @Override
    public int size() {
        if (data == null) {
            return 0;
        }
        return last;
    }

    @Override
    public boolean isEmpty() {
        return (last == 0);
    }

    @Override
    public void setCapacity(int capacity) {
        if (data.length >= capacity) {
            return;
        }
        long[] newData = new long[capacity];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public int getCapacity() {
        return data.length;
    }

    @Override
    public Instance copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        for (int i = 0; i < size(); i++) {
            res[i] = value(i);
        }
        return res;
    }

    @Override
    public double[] asArray() {
        return arrayCopy();
    }

    @Override
    public String toString(String separator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long getValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int index) {
        if (index < 0 || index >= last) {
            return unknown;
        }
        return data[index];
    }

    @Override
    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int index, Number value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Long> add(Vector<Long> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Long> minus(Vector<Long> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Long> times(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Long> duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Long> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
