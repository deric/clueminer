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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.math.Vector;

/**
 * Sparse instance stores data in HashMap which is suitable for instances with
 * either unknown number of attributes or number that vary for each instance in
 * a set
 *
 * @author Tomas Barton
 */
public class SparseInstance extends DataRow implements Instance, Iterable<Double> {

    private static final long serialVersionUID = 6915224136652316915L;
    /**
     * @TODO consider performance when replacing with SortedMap<whatever>
     * myNewMap = new TreeMap<whatever>(myOldMap);
     */
    private HashMap<Integer, Double> data = new HashMap<>();
    private double defaultValue;
    /**
     * @FIXME this is handy especially when converting dataset to other formats
     * there should be defined interval for sparse keys
     */
    private int minKey = Integer.MAX_VALUE;
    private int maxKey = Integer.MIN_VALUE;

    public SparseInstance() {
        this(-1);
    }

    /*
     * defaultValue will be set to 0.0, classValue to null
     */
    public SparseInstance(int noAttributes) {
        this(noAttributes, 0.0, null);
    }

    /*
     * Create empty sparse instance
     */
    public SparseInstance(int noAttributes, double defaultValue, Object classValue) {
        super(classValue);
        this.defaultValue = defaultValue;
    }

    public SparseInstance(double[] datavector, double defaultValue, Object classValue) {
        super(classValue);
        this.defaultValue = defaultValue;
        initiate(datavector);
    }

    @Override
    public String getFullName() {
        return getId() + " - " + getName();
    }

    private void initiate(double[] datavector) {
        data.clear();
        for (int i = 0; i < datavector.length; i++) {
            if (datavector[i] != defaultValue) {
                set(i, datavector[i]);
            }
        }
    }

    /**
     * Insert value and return its index, this method will NOT override existing
     * values, instead it will try to place the value at next available position
     *
     * @param value
     * @return
     */
    @Override
    public int put(double value) {
        int idx = maxKey + 1;
        while (data.containsKey(idx)) {
            idx++;
        }
        set(idx, value);
        checkIndexInterval(idx);
        return this.size();
    }

    /**
     *
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
     * if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return can
     * also indicate that the map previously associated <tt>null</tt> with
     * <tt>key</tt>.)
     */
    public Double put(Integer key, Double value) {
        return data.put(key, value);
    }

    public void putAll(Map<? extends Integer, ? extends Double> m) {
        data.putAll(m);
    }

    @Override
    public void remove(int i) {
        //@TODO remove returns the previous value associated with <tt>key</tt>
        data.remove(i);
    }

    /**
     * Simple get operations, index bounds are not checked
     *
     * @param index
     * @return
     */
    @Override
    public double value(int index) {
        return data.get(index);
    }

    @Override
    public double get(int index) {
        return data.get(index);
    }

    @Override
    protected double getValue(int index, double defaultValue) {
        if (data.containsKey(index)) {
            return data.get(index);
        } else {
            return defaultValue;
        }
    }

    @Override
    protected void setValue(int index, double value, double defaultValue) {
        if (value == Double.NaN) {
            data.put(index, defaultValue);
        } else {
            data.put(index, value);
        }
    }

    @Override
    public void set(int index, double value) {
        data.put(index, value);
        checkIndexInterval(index);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Capacity of sparse instance if unlimited, no need to use this method
     *
     * @param capacity
     */
    @Override
    public void setCapacity(int capacity) {
        throw new UnsupportedOperationException("Capacity of sparse instance if unlimited");
    }

    /**
     * Capacity of sparse instance if unlimited
     *
     * @return
     */
    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("Capacity of sparse instance if unlimited");
    }

    @Override
    public int hashCode() {
        final int prime = 127;
        int result = 1;
        //instances having same values should not be considered as the same
        if (index >= 0) {
            result += (index + prime);
        }
        result *= ((data == null) ? 0 : data.hashCode());
        long temp;
        temp = Double.doubleToLongBits(defaultValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        final SparseInstance other = (SparseInstance) obj;
        if (this.getIndex() != other.getIndex()) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        return Double.doubleToLongBits(defaultValue) == Double.doubleToLongBits(other.defaultValue);
    }

    @Override
    public Instance copy() {
        SparseInstance out = new SparseInstance();
        out.data = new HashMap<>();
        out.data.putAll(this.data);
        out.defaultValue = this.defaultValue;
        out.setClassValue(this.classValue());
        return out;

    }

    @Override
    public void trim() {
        throw new UnsupportedOperationException("No need for triming");
    }

    @Override
    public String toString() {
        return "{" + data.toString() + ";" + classValue() + "}";
    }

    /**
     * Method for conversion of spatial data to comma separated (which is not
     * ideal, but might solve compatibility problems)
     *
     * @param separator
     * @return
     */
    @Override
    public String toString(String separator) {
        List<Integer> keys = new ArrayList<>(data.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        int i = minKey; //first
        int last = keys.get(keys.size() - 1);
        int j = 0;
        int next = keys.get(j);
        while (i < last) {
            //fills the gap between no data with separators
            while (i < next) {
                sb.append(separator);
                i++;
            }
            sb.append(data.get(keys.get(j++))).append(separator);
            if (j < keys.size()) {
                next = keys.get(j);
            }
        }
        return sb.toString();
    }

    /**
     * EXTRA METHODS, SPECIFIC FOR COLLECTIONS (might be in the future part of
     * searchable sets interface)
     */
    public void clear() {
        data.clear();
    }

    public boolean containsKey(int key) {
        return data.containsKey(key);
    }

    public boolean containsValue(double value) {
        return data.containsValue(value);
    }

    public Set<java.util.Map.Entry<Integer, Double>> entrySet() {
        return data.entrySet();
    }

    public TreeSet<Integer> keySet() {
        TreeSet<Integer> set = new TreeSet<>();
        set.addAll(data.keySet());
        return set;
    }

    public Collection<Double> valuesCollection() {
        return data.values();
    }

    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        int i = 0;
        for (double d : data.values()) {
            res[i++] = d;
        }
        return res;
    }

    @Override
    public double[] asArray() {
        return arrayCopy();
    }

    private void checkIndexInterval(int index) {
        if (index < minKey) {
            minKey = index;
        }
        if (index > maxKey) {
            maxKey = index;
        }
    }

    @Override
    public Iterator<Double> iterator() {
        return new SparseInstanceIterator();
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Number getValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public org.clueminer.math.Vector add(org.clueminer.math.Vector other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.clueminer.math.Vector duplicate() {
        return new SparseInstance(this.size());
    }

    @Override
    public Vector minus(Vector other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector times(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setObject(int index, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class SparseInstanceIterator implements Iterator<Double> {

        private final Iterator<Integer> it;

        public SparseInstanceIterator() {
            it = data.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Double next() {
            return value(it.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }
}
