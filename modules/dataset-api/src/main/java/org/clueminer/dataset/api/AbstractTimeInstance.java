package org.clueminer.dataset.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <I>
 */
public abstract class AbstractTimeInstance<E extends Number> extends AbstractInstance<E> implements ContinuousInstance<E>, Iterable<E>, Vector<E> {

    private static final long serialVersionUID = 7144673261130372477L;
    protected Instance ancestor;
    //real count
    protected int last = 0;
    protected long startTime;
    protected static final Logger logger = Logger.getLogger(AbstractTimeInstance.class.getName());
    protected double[] meta;
    /**
     * Contains all attribute statistics calculation algorithms.
     */
    protected List<Statistics> statistics = new LinkedList<Statistics>();
    /**
     * Mapping of attributes to its providers
     */
    protected HashMap<IStats, Statistics> statisticsProviders = new HashMap<IStats, Statistics>();

    public AbstractTimeInstance() {
    }

    public AbstractTimeInstance(Timeseries<? extends ContinuousInstance> parent) {
        setParent(parent);
    }

    protected void checkMinMax(double val) {
        updateStatistics(val);
    }

    @Override
    public int size() {
        return last;
    }

    @Override
    public boolean isEmpty() {
        return (last == 0);
    }

    /**
     * Data loaded from Access are not necessarily sorted by index so the actual
     * count might not correspondent with highest id number
     *
     * @param idx
     * @return
     */
    public boolean contains(int idx) {
        return (idx < size() && idx >= 0);
    }

    public abstract E item(int index);

    public void resetMinMax() {
        E dc;
        resetStatistics();
        for (int i = 0; i < size(); i++) {
            dc = item(i);
            if (dc != null) {
                updateStatistics(dc.doubleValue());
            } else {
                logger.log(Level.INFO, "data-item at pos = {0} is null, instance size = {1}", new Object[]{i, size()});
                System.out.println(toString());
            }
        }
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    /**
     * @TODO maybe store array copy to a variable, but it might use too much
     * memory, this way it can be deallocated quickly
     *
     * @return
     */
    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        for (int i = 0; i < size(); i++) {
            res[i] = this.value(i);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double magnitude() {
        double m = 0;
        int length = size();
        for (int i = 0; i < length; ++i) {
            double d = get(i);
            m += d * d;
        }
        return Math.sqrt(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double dot(Vector v) {
        if (this.size() != v.size()) {
            throw new ArithmeticException("Vectors must have the same length" + this.size() + " != " + v.size());
        }
        double dot = 0.0;
        for (int i = 0; i < this.size(); i++) {
            dot += this.get(i) * v.get(i);
        }

        return dot;
    }

    @Override
    public double pNorm(double p) {
        double norm = 0;
        for (int i = 0; i < size(); i++) {
            norm += Math.pow(Math.abs(get(i)), p);
        }

        return Math.pow(norm, 1.0 / p);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<E> add(double num) {
        Vector<E> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) + num);
        }
        return res;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<E> minus(double num) {
        Vector<E> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, this.get(i) - num);
        }
        return res;
    }

    /**
     * When preprocessing data sometimes we need to display reference to
     * original data
     *
     * @return Instance from which was this one derived
     */
    @Override
    public Instance getAncestor() {
        return ancestor;
    }

    /**
     * Set reference to original data row
     *
     * @param instance
     */
    @Override
    public void setAncestor(Instance instance) {
        this.ancestor = instance;
    }

    @Override
    public double[] getMetaNum() {
        return meta;
    }

    @Override
    public void setMetaNum(double[] meta) {
        this.meta = meta;
    }

    @Override
    public Iterator<Statistics> getAllStatistics() {
        return statistics.iterator();
    }

    @Override
    public void registerStatistics(Statistics statistics) {
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
    public void resetStatistics() {
        for (Statistics s : statistics) {
            s.reset();
        }
    }
}
