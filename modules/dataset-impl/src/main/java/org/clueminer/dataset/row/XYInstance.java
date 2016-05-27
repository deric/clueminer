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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.AbstractInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Vector;
import org.clueminer.stats.NumericalStats;
import org.clueminer.dataset.api.Stats;

/**
 * Continuous representation of two variables (e.g. time and speed).
 *
 * @author deric
 */
public class XYInstance extends AbstractInstance<Double> implements Instance<Double>, ContinuousInstance<Double>, Iterable<Double> {

    private double[] x;
    private double[] y;
    protected static final Logger logger = Logger.getLogger(XYInstance.class.getName());
    /**
     * Contains all attribute statistics calculation algorithms.
     */
    protected List<Statistics> statistics = new LinkedList<Statistics>();
    /**
     * Mapping of attributes to its providers
     */
    protected HashMap<Stats, Statistics> statisticsProviders = new HashMap<Stats, Statistics>();
    protected int last;
    protected Instance ancestor;

    public XYInstance(int capacity) {
        x = new double[capacity];
        y = new double[capacity];
        last = 0; //index of next value
        registerStatistics(new NumericalStats(this));
        resetMinMax();
    }

    public void resetMinMax() {
        double dc;
        resetStatistics();
        for (int i = 0; i < size(); i++) {
            dc = y[i];
            if (Double.isFinite(dc)) {
                updateStatistics(dc);
            } else {
                logger.log(Level.INFO, "data-item at pos = {0} is null, instance size = {1}", new Object[]{i, size()});
            }
        }
    }

    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public Iterator<Statistics> getAllStatistics() {
        return statistics.iterator();
    }

    @Override
    public void registerStatistics(Statistics statistics) {
        this.statistics.add(statistics);
        Stats[] stats = statistics.provides();
        for (Stats stat : stats) {
            statisticsProviders.put(stat, statistics);
        }
    }

    @Override
    public double statistics(Stats name) {
        if (statisticsProviders.containsKey(name)) {
            return statisticsProviders.get(name).statistics(name);
        }
        throw new RuntimeException("statistics " + name + " was not registered");
    }

    /**
     * So far we have statistics only for Y values
     *
     * @param value
     */
    @Override
    public void updateStatistics(double value) {
        for (Statistics s : statistics) {
            s.valueAdded(value);
        }
    }

    @Override
    public void resetStatistics() {
        for (Statistics s : statistics) {
            s.reset();
        }
    }

    /**
     * X and Y must have the same length.
     *
     * @return
     */
    @Override
    public int size() {
        return last;
    }

    @Override
    public boolean isEmpty() {
        return last == 0;
    }

    @Override
    public double[] arrayCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getMetaNum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMetaNum(double[] meta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Instance getAncestor() {
        return ancestor;
    }

    @Override
    public void setAncestor(Instance instance) {
        this.ancestor = instance;
    }

    @Override
    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double dot(Vector v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double pNorm(double p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getStartTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int put(double value) {
        updateStatistics(value);
        if (last >= getCapacity()) {
            int req = (int) (last * 1.618);
            if (req <= last) {
                req = last + 1;
            }
            setCapacity(req);
        }
        //TODO: inconsitent operation! we're not modifing X!
        y[last++] = value;
        return last;
    }

    /**
     * Add new pair of values [x, y]
     *
     * @param x
     * @param y
     * @return
     */
    public int put(double x, double y) {
        updateStatistics(y);
        if (last >= getCapacity()) {
            int req = (int) (last * 1.618);
            if (req <= last) {
                req = last + 1;
            }
            setCapacity(req);
        }
        this.x[last] = x;
        this.y[last] = y;
        return last++;
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double value(int index) {
        return get(index);
    }

    @Override
    public void set(int index, double value) {
        y[index] = value;
    }

    @Override
    public void setCapacity(int capacity) {
        if (x.length >= capacity) {
            return;
        }
        double[] nx = new double[capacity];
        System.arraycopy(x, 0, nx, 0, x.length);
        x = nx;
        double[] ny = new double[capacity];
        System.arraycopy(y, 0, ny, 0, y.length);
        y = ny;
    }

    @Override
    public int getCapacity() {
        return y.length;
    }

    @Override
    public String toString(String separator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public Double getValue(int index) {
        return get(index);
    }

    /**
     *
     * @param idx
     * @return true when index present
     */
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < size();
    }

    @Override
    public double get(int index) {
        return y[index];
    }

    @Override
    public void set(int index, Number value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> minus(Vector<Double> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> times(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<? extends Object> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getStdDev() {
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
    public Iterator<Double> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> add(double num) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Double> minus(double num) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("XY(").append(size()).append(")").append("[");
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(x[i]).append(":").append(y[i]);
        }
        sb.append("]");
        return sb.toString();
    }

}
