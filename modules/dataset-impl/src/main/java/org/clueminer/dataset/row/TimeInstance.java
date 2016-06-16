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

import java.util.HashMap;
import java.util.Iterator;
import org.clueminer.algorithm.InterpolationSearch;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Vector;
import org.clueminer.stats.NumericalStats;
import org.clueminer.types.TimePoint;

/**
 * Time dataset that allows having multiple data values per each time in one
 * instance. e.g. in stock market you have typically opening, closing value etc.
 *
 * @author Tomas Barton
 * @param <E>
 */
public class TimeInstance<E extends DataItem> extends AbstractTimeInstance<E> implements Instance<E>, ContinuousInstance<E>, Iterable<E> {

    private static final long serialVersionUID = -1881645784146059894L;
    protected DataItem[] data;
    /**
     * Specific coefficients that should be descriptive for dataset as whole (in
     * dataset could be thousands of values, we would like to simplify the huge
     * amount of data)
     *
     * @TODO this should be treated as a new dataset, where coefficients are
     * attributes
     */
    private HashMap<String, Double> coefficients = new HashMap<>();
    private boolean isApproximated = false;
    private Interpolator interpolator;

    /**
     * When instance is added to a dataset, the parent should be set up - parent
     * has a reference to time points
     *
     * @param capacity
     */
    public TimeInstance(int capacity) {
        data = new DataItem[capacity];
        registerStatistics(new NumericalStats(this));
    }

    @Override
    public String getFullName() {
        return getId() + " - " + getName();
    }

    @Override
    public int put(double value) {
        if (last >= size()) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.5));
        }
        checkMinMax(value);
        data[last++] = new DataItem(value);
        return last;
    }

    public int put(DataItem value) {
        if (last >= size()) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.5));
        }
        checkMinMax(value.getValue());
        data[last++] = value;
        return last;
    }

    @Override
    public int getCapacity() {
        return data.length;
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double value(int index) {
        return data[index].value;
    }

    @Override
    public E getValue(int index) {
        return (E) data[index];
    }

    @Override
    public double get(int index) {
        return data[index].value;
    }

    @Override
    public E item(int index) {
        return (E) data[index];
    }

    @Override
    public void set(int index, double value) {
        if (index >= size()) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (index * 1.5));
        }
        checkMinMax(value);
        if (index > last) {
            last = index;
        }
        data[index] = new DataItem(value);
    }

    @Override
    public void setCapacity(int capacity) {
        if (data.length >= capacity) {
            return;
        }
        DataItem[] newData = new DataItem[capacity];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    public double getCoefficient(String key) {
        if (coefficients.containsKey(key)) {
            return coefficients.get(key);
        }
        if (isApproximated) {
            throw new UnsupportedOperationException("coefficient not found");
        }
        return 0;
    }

    public HashMap<String, Double> getCoefficients() {
        return coefficients;
    }

    @Override
    public void crop(int begin, int size) {
        DataItem[] dataNew = new DataItem[size];
        last = 0;
        //from array, start pos, target, position in copy, length
        System.arraycopy(data, begin, dataNew, 0, size);
        data = dataNew;
        last = size;
        resetMinMax();
    }

    /**
     * Normalize data to given DataItem index
     *
     * @param idx
     */
    @Override
    public void normalize(int idx) {
        resetStatistics();
        double normPoint = value(idx);
        DataItem dc;
        for (int i = 0; i < size(); i++) {
            dc = data[i];
            dc.setValue(dc.value / normPoint);
            checkMinMax(dc.getValue());
        }
    }

    /**
     * We could copy reference to that array to each instance, however this
     * makes it much more clear, that time points are shared between all
     * instances in dataset
     *
     * @return
     */
    public TimePoint[] getTimePoints() {
        return ((Timeseries) parent).getTimePoints();
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
        TimePointAttribute[] timePoints = (TimePointAttribute[]) getTimePoints();
        int idx = InterpolationSearch.search(timePoints, x);
        int low, up;
        if (timePoints[idx].getValue() > x) {
            up = idx;
            low = idx - 1;
        } else if (timePoints[idx].getValue() < x) {
            low = idx;
            up = idx + 1;
        } else {
            //exact match
            return data[idx].getValue();
        }
        if (!interpolator.hasData()) {
            interpolator.setX(timePoints);
            interpolator.setY((Number[]) data);
        }
        return interpolator.value(x, low, up);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("DataItem [ \n");
        for (int i = 0; i < size(); i++) {
            str.append(this.item(i).toString());
        }
        str.append("\n ]");
        return str.toString();
    }

    @Override
    public String toString(String separator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ContinuousInstance copy() {
        TimeInstance<E> c = new TimeInstance<>(size());
        c.setParent(this.getParent());
        c.name = this.name;
        c.classValue = this.classValue;
        //@TODO coefficient are only a shallow copy
        c.coefficients = (HashMap<String, Double>) this.coefficients.clone();
        for (int i = 0; i < size(); i++) {
            c.set(i, this.value(i));
        }
        return c;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double valueAt(double x) {
        if (interpolator == null) {
            interpolator = new LinearInterpolator();
        }
        return valueAt(x, interpolator);
    }

    @Override
    public String[] toStringArray() {
        String[] res = new String[size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = String.valueOf(value(i));
        }
        return res;
    }

    @Override
    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int index, Number value) {
        set(index, value.doubleValue());
    }

    @Override
    public void setObject(int index, Object value) {
        set(index, (double) value);
    }

    private void checkForSameSize(Vector other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Vectors of different sizes cannot be added");
        }
    }

    @Override
    public Vector add(Vector<E> other) {
        checkForSameSize(other);
        Vector<E> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, value(i) + other.get(i));
        }
        return res;
    }

    @Override
    public Vector<E> minus(Vector<E> other) {
        checkForSameSize(other);
        Vector<E> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, value(i) - other.get(i));
        }
        return res;
    }

    @Override
    public Vector<E> times(double scalar) {
        Vector<E> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, value(i) * scalar);
        }
        return res;
    }

    @Override
    public double getMax() {
        return statistics(StatsNum.MAX);
    }

    @Override
    public double getMin() {
        return statistics(StatsNum.MIN);
    }

    @Override
    public Iterator<? extends Object> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getStdDev() {
        return statistics(StatsNum.STD_DEV);
    }

    @Override
    public Vector<E> duplicate() {
        return new TimeInstance(this.size());
    }

}
