package org.clueminer.dataset.api;

import java.util.Iterator;
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

    public double getStdDev();

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
     * Returns an iterator over all statistics objects available for this type
     * of instance. Additional statistics can be registered via
     * {@link #registerStatistics(Statistics)}.
     *
     * @return
     */
    public Iterator<Statistics> getAllStatistics();

    /**
     * Registers the instance statistics.
     *
     * @param statistics
     */
    public void registerStatistics(Statistics statistics);

    /**
     * Return value of precomputed statistics, which should be on changes in
     * dataset updated
     *
     * @param name
     * @return
     */
    public double statistics(Stats name);

    /**
     * Triggered when a new value is added to the instance. The type of a value
     * should be determined by the Attribute itself
     *
     *
     * @param value
     */
    public void updateStatistics(double value);

    /**
     * Reset all registered statistics
     *
     */
    public void resetStatistics();

}
