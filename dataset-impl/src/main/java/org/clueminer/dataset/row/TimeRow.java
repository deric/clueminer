package org.clueminer.dataset.row;

import java.lang.reflect.Array;
import java.util.Iterator;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.plot.TimePlot;
import org.clueminer.interpolation.InterpolationSearch;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Vector;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.stats.NumericalStats;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class TimeRow<E extends Number> extends AbstractTimeInstance<E> implements Instance<E>, ContinuousInstance<E>, Iterable<E> {

    private static final long serialVersionUID = 6410706965541438907L;
    private Interpolator interpolator = new LinearInterpolator();
    private E[] data;
    protected TimePointAttribute[] timePoints;
    private Iterator<E> it;
    private double defaultValue = Double.NaN;

    public TimeRow(Class<E> klass, int capacity) {
        data = (E[]) Array.newInstance(klass, capacity);
        registerStatistics(new NumericalStats(this));
        resetMinMax();
    }

    @Override
    public E item(int index) {
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
        //data.ensureCapacity(capacity);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public String[] toStringArray() {
        String[] result = new String[size()];

        for (int i = 0; i < size(); i++) {
            result[i] = String.valueOf(get(i));
        }
        return result;
    }

    @Override
    public Plotter getPlotter() {
        TimePlot plot = new TimePlot();
        // add a line plot to the PlotPanel
        plot.addLinePlot(getName(), parent.getTimePointsArray(), this.arrayCopy());
        return plot;
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
    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int index, Number value) {
        set(index, value.doubleValue());
    }

    @Override
    public Vector<E> add(Vector<E> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        //  return interpolator.getValue(timePoints, data, x, low, up);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void crop(int begin, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return statistics(AttrNumStats.MAX);
    }

    @Override
    public double getMin() {
        return statistics(AttrNumStats.MIN);
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
    public String toString() {
        return "TimeRow[" + size() + "] " + toString(",");
    }
}
