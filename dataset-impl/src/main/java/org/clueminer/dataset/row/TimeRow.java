package org.clueminer.dataset.row;

import java.util.ArrayList;
import java.util.Iterator;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.interpolation.InterpolationSearch;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public class TimeRow<E extends Number> extends AbstractTimeInstance<E> implements Instance<E>, ContinuousInstance<E>, Iterable<E> {

    private static final long serialVersionUID = 6410706965541438907L;
    private Interpolator interpolator = new LinearInterpolator();
    private ArrayList<E> data;
    private TimePointAttribute[] timePoints;

    public TimeRow(int capacity) {
        data = new ArrayList<E>(capacity);
    }

    @Override
    public E item(int index) {
        return data.get(index);
    }

    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public int put(double value) {
        Number v = value;
        data.add((E) v);
        return data.size();
    }

    @Override
    public void remove(int index) {
        data.remove(index);
    }

    @Override
    public double value(int index) {
        return data.get(index).doubleValue();
    }

    @Override
    public void set(int index, double value) {
        Number element = value;
        data.set(index, (E) element);
    }

    @Override
    public void setCapacity(int capacity) {
        data.ensureCapacity(capacity);
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
            sb.append(data.get(i).toString());
        }
        return sb.toString();
    }

    @Override
    public String[] toStringArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E getValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int index) {
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
    public Vector<E> add(Vector<E> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x) {
        return valueAt(x, interpolator);
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
      /*  int idx = InterpolationSearch.search(timePoints, x);
        int low, up;
        if (timePoints[idx].getValue() > x) {
            up = idx;
            low = idx - 1;
        } else if (timePoints[idx].getValue() < x) {
            low = idx;
            up = idx + 1;
        } else {
            //exact match
            return data.get(idx).doubleValue();
        }
        return interpolator.getValue(timePoints, data, x, low, up);*/
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
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
