package org.clueminer.dataset.api;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class AbstractTimeInstance<E extends Number> extends AbstractInstance<E> implements ContinuousInstance<E>, Iterable<E> {

    private static final long serialVersionUID = 7144673261130372477L;
    protected double max = Double.MIN_VALUE;
    protected double min = Double.MAX_VALUE;
    protected Timeseries<? extends ContinuousInstance> parent;
    protected Instance ancestor;
    //real count
    protected int last = 0;
    protected long startTime;

    public AbstractTimeInstance() {
    }

    public AbstractTimeInstance(Timeseries<? extends ContinuousInstance> parent) {
        setParent(parent);
    }

    protected void checkMinMax(double val) {
        if (val > this.max) {
            this.max = val;
        }
        if (val < min) {
            min = val;
        }
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
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        E dc;
        for (int i = 0; i < size(); i++) {
            dc = item(i);
            checkMinMax(dc.doubleValue());
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

    @Override
    public double getMax() {
        if (max == Double.MIN_VALUE) {
            resetMinMax();
        }
        return max;
    }

    @Override
    public double getMin() {
        if (min == Double.MAX_VALUE) {
            resetMinMax();
        }
        return min;
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getParent() {
        return parent;
    }

    @Override
    public final void setParent(Timeseries<? extends ContinuousInstance> parent) {
        this.parent = parent;
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
}
