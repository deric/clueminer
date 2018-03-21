/*
 * Copyright (C) 2011-2018 clueminer.org
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

import java.lang.reflect.Array;
import java.util.Iterator;
import org.clueminer.algorithm.InterpolationSearch;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.PlotterFactory;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Vector;
import org.clueminer.stats.NumericalStats;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class TimeRow<E extends Number> extends AbstractTimeInstance<E> implements Instance<E>, ContinuousInstance<E>, Iterable<E> {

    private static final long serialVersionUID = 6410706965541438908L;
    private Interpolator interpolator = new LinearInterpolator();
    private E[] data;
    protected TimePointAttribute[] timePoints;
    private Iterator<E> it;
    private final Class<E> klass;
    private double defaultValue = Double.NaN;
    private static final Logger LOG = LoggerFactory.getLogger(TimeRow.class);

    public TimeRow(Class<E> klass, int capacity) {
        data = (E[]) Array.newInstance(klass, capacity);
        this.klass = klass;
        registerStatistics(new NumericalStats(this));
        resetMinMax();
    }

    @Override
    public E item(int index) {
        if (data[index] == null) {
            Number num = defaultValue;
            return (E) num;
        }
        return data[index];
    }

    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public int put(double value) {
        updateStatistics(value);
        Number v = value;
        if (last >= getCapacity()) {
            int req = (int) (last * 1.618);
            if (req <= last) {
                req = last + 1;
            }
            setCapacity(req);
        }
        data[last++] = (E) v;
        return last;
    }

    @Override
    public void remove(int index) {
        throw new UnsupportedOperationException("Remove is not supported for an array storage");
    }

    @Override
    public double value(int index) {
        return item(index).doubleValue();
    }

    /**
     * Sets value at given index
     *
     * We might set value in middle of an array (values before are filled with
     * defaultValues)
     *
     * @param index
     * @param value
     */
    @Override
    public void set(int index, double value) {
        Number element = value;
        data[index] = (E) element;
        if (!hasIndex(index)) {
            //increase current number of values
            last = index + 1;
        }
        updateStatistics(value);
    }

    @Override
    public void setCapacity(int capacity) {
        if (data.length >= capacity) {
            return;
        }
        E[] newData = (E[]) Array.newInstance(klass, capacity);
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public int getCapacity() {
        return data.length;
    }

    @Override
    public String[] toStringArray() {
        String[] result = new String[size()];

        for (int i = 0; i < size(); i++) {
            result[i] = String.valueOf(get(i));
        }
        return result;
    }

    /**
     * Base data type can't be depenet on GUI modules. Here we need to find
     * appropriate implementation of visualization, if available.
     *
     * TODO: pass props to allow choosing preferred renderer
     *
     * @return
     */
    @Override
    public Plotter getPlotter(Props props) {
        /* TimePlot plot = new TimePlot();
         * // add a line plot to the PlotPanel
         * plot.addLinePlot(getName(), ((Timeseries) parent).getTimePointsArray(), this.arrayCopy());
         * return plot; */
        PlotterFactory factory = PlotterFactory.getInstance();
        for (Plotter p : factory.getAll()) {
            if (p.isSupported(DataType.TIMESERIES)) {
                p.addInstance(this);
                return p;
            }
        }
        throw new RuntimeException("No visualization found for data type " + this.getClass().getName());
    }

    @Override
    public E getValue(int index) {
        return item(index);
    }

    /**
     *
     * @param idx
     * @return true when index present
     */
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < size();
    }

    /**
     * Return double value at given index, if value is not set (null) will
     * return *defaultValue*
     *
     * @param index
     * @return
     */
    @Override
    public double get(int index) {
        if (hasIndex(index)) {
            return item(index).doubleValue();
        }
        return defaultValue;
    }

    @Override
    public void set(int index, Number value) {
        set(index, value.doubleValue());
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

    /**
     * Multiply by given factor and return new instance of TimeRo
     *
     * @param factor
     * @return
     */
    public TimeRow<E> multiply(double factor) {
        TimeRow<E> res = new TimeRow(Double.class, this.size());
        res.timePoints = this.timePoints; // TODO: implement duplicate method

        for (int i = 0; i < size(); i++) {
            res.put(this.get(i) * factor);
        }
        return res;
    }

    @Override
    public double valueAt(double x) {
        return valueAt(x, interpolator);
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
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
            return item(idx).doubleValue();
        }
        if (!interpolator.hasData()) {
            interpolator.setX(timePoints);
            interpolator.setY((Number[]) data);
        }
        return interpolator.value(x, low, up);
    }

    @Override
    public void setParent(Dataset parent) {
        super.setParent(parent);
        Timeseries ts = (Timeseries) parent;
        this.timePoints = (TimePointAttribute[]) ts.getTimePoints();
    }

    @Override
    public TimeRow crop(int begin, int end) {
        TimeRow inst = new TimeRow(this.klass, end - begin + 1);
        for (int i = begin; i <= end; i++) {
            inst.set(i - begin, this.getValue(i));
        }
        return inst;
    }

    @Override
    public void normalize(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContinuousInstance copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<? extends Object> values() {
        return new InstanceValueIterator();
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
    public double getStdDev() {
        return statistics(StatsNum.STD_DEV);
    }

    @Override
    public Iterator<E> iterator() {
        if (it == null) {
            it = new InstanceValueIterator();
        }
        return it;
    }

    @Override
    public int size() {
        return last;
    }

    /**
     * Default value is returned in case that value at requested position is
     * unknown
     *
     * @param value
     */
    public void setDefaultValue(double value) {
        this.defaultValue = value;
    }

    @Override
    public Vector<E> duplicate() {
        return new TimeRow(this.klass, this.size());
    }

    @Override
    public String toString() {
        return "TimeRow[" + size() + "] " + toString(",");
    }

    @Override
    public String toString(String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < last; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(String.format("%.2f", item(i)));
        }
        return sb.toString();
    }

    @Override
    public void setObject(int index, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] toArray() {
        double[] res = new double[size()];
        System.arraycopy(data, 0, res, 0, size());
        return res;
    }

    class InstanceValueIterator<E extends Number> implements Iterator<E> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }

        @Override
        public E next() {
            index++;
            return (E) getValue(index - 1);
        }
    }
}
