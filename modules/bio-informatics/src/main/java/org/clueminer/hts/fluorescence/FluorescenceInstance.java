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
package org.clueminer.hts.fluorescence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.dataset.api.Stats;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.row.IntegerDataRow;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.math.Interpolator;

/**
 *
 * @author Tomas Barton
 */
public class FluorescenceInstance extends IntegerDataRow implements ContinuousInstance<Integer>, HtsInstance<Integer> {

    private static final long serialVersionUID = -7474451177647880896L;
    private int row;
    private int col;
    private Timeseries<? extends ContinuousInstance> parent;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;
    /**
     * Contains all attribute statistics calculation algorithms.
     */
    protected List<Statistics> statistics = new LinkedList<Statistics>();
    /**
     * Mapping of attributes to its providers
     */
    protected HashMap<Stats, Statistics> statisticsProviders = new HashMap<Stats, Statistics>();

    public FluorescenceInstance(int size) {
        super(size);
    }

    public FluorescenceInstance(Timeseries<? extends ContinuousInstance> plate, int capacity) {
        super(capacity);
        parent = plate;
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
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public void set(int index, Number value) {
        checkMinMax(value.intValue());
        super.set(index, value);
    }

    @Override
    public void set(int index, double value) {
        checkMinMax((int) value);
        super.set(index, value);
    }

    private void checkMinMax(int value) {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    @Override
    public int put(int value) {
        checkMinMax(value);
        return super.put(value);
    }

    @Override
    protected void setValue(int index, double value, double defaultValue) {
        checkMinMax((int) value);
        super.setValue(index, value, defaultValue);
    }

    @Override
    public int put(double value) {
        checkMinMax((int) value);
        return super.put(value);
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
    public long getStartTime() {
        if (parent.attributeCount() == 0) {
            throw new RuntimeException("no attributes in dataset!");
        }
        return ((TimePointAttribute) parent.getAttribute(0)).getTimestamp();
    }

    @Override
    public ContinuousInstance copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParent(Dataset parent) {
        this.parent = (Timeseries<? extends ContinuousInstance>) parent;
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getParent() {
        return parent;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int col) {
        this.col = col;
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Plotter getPlotter() {
        FluorescencePlot plot = new FluorescencePlot();
        // add a line plot to the PlotPanel
        plot.addLinePlot(getName(), parent.getTimePointsArray(), this.arrayCopy());
        return plot;
    }

    public String[] toArray() {
        String[] res = new String[size() + 2];
        res[0] = getName();
        res[1] = getId();
        for (int i = 0; i < res.length; i++) {
            res[i + 2] = String.valueOf(intValue(i));

        }
        return res;
    }

    /**
     * not supported yet
     *
     * @return
     */
    @Override
    public boolean hasFormula() {
        return false;
    }

    @Override
    public String getSmiles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public void updateStatistics(double value) {
        for (Statistics s : statistics) {
            s.valueAdded(value);
        }
    }

    @Override
    public Iterator<? extends Object> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetStatistics() {
        for (Statistics s : statistics) {
            s.reset();
        }
    }

    @Override
    public double getStdDev() {
        return statistics(StatsNum.STD_DEV);
    }
}
