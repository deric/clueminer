package org.clueminer.posturomed;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.IStats;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.row.IntegerDataRow;
import org.clueminer.math.Interpolator;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.stats.NumericalStats;

/**
 *
 * @author Tomas Barton
 */
public class PosturomedInstance extends IntegerDataRow implements Instance<Integer>, ContinuousInstance<Integer> {

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
    protected HashMap<IStats, Statistics> statisticsProviders = new HashMap<IStats, Statistics>();

    public PosturomedInstance(int size) {
        super(size);
        registerStatistics(new NumericalStats(this));
    }

    public PosturomedInstance(Timeseries<? extends ContinuousInstance> plate, int capacity) {
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
    public void setParent(Timeseries parent) {
        this.parent = parent;
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getParent() {
        return parent;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int col) {
        this.col = col;
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    @Override
    public Iterator<Statistics> getAllStatistics() {
        return statistics.iterator();
    }

    @Override
    public final void registerStatistics(Statistics statistics) {
        this.statistics.add(statistics);
        IStats[] stats = statistics.provides();
        for (IStats stat : stats) {
            statisticsProviders.put(stat, statistics);
        }
    }

    @Override
    public double statistics(IStats name) {
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
        return statistics(AttrNumStats.STD_DEV);
    }

    public void resetStats() {
        int val;
        resetStatistics();
        for (int i = 0; i < size(); i++) {
            val = (int) value(i);
            updateStatistics(val);
        }
    }
}
