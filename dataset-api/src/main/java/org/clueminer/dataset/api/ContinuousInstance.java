package org.clueminer.dataset.api;

import org.clueminer.math.Interpolator;

/**
 * Represent data row that has continuous character (a value can be obtained for
 * any point between begin and end)
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ContinuousInstance<E extends Number> extends Instance<E> {

    public double valueAt(double x);

    public double valueAt(double x, Interpolator interpolator);

    public double getMin();

    public double getMax();

    public void crop(int begin, int size);

    /**
     * Normalize data to given time point at index
     *
     * @param index
     */
    public void normalize(int index);

    public long getStartTime();

    @Override
    public ContinuousInstance copy();

    /**
     * Set parent dataset which is used for storing time points, that are shared
     * between instances
     *
     * @param parent
     */
    public void setParent(Timeseries<? extends ContinuousInstance> parent);

    public Timeseries<? extends ContinuousInstance> getParent();
}
