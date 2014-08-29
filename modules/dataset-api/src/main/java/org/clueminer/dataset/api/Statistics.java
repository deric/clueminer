package org.clueminer.dataset.api;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface Statistics extends Serializable, Cloneable {

    public Object clone();

    public void reset();

    /**
     * Update statistics with a new value
     *
     * @param value
     */
    public void valueAdded(double value);

    /**
     * A value was removed from a dataset
     *
     * @param value
     */
    public void valueRemoved(double value);

    /**
     * Array of statistics which are provided by the class
     *
     * @return
     */
    public IStats[] provides();

    /**
     * Value of the statistics with given name. The name should be unique within
     * all statistics providers
     *
     * @param name
     * @return
     */
    public double statistics(IStats name);

    /**
     * Returns the specified information for the whole data source.
     *
     * @param key Requested information.
     * @return The value for the specified key as value, or <i>NaN</i>
     * if the specified statistical value does not exist
     */
    public double get(String key);
}
