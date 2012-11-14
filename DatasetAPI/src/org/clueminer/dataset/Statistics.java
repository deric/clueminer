package org.clueminer.dataset;

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
     * @param statistics
     */
    public void valueAdded(double value);
    
    
    /**
     * A value was removed from a dataset
     *
     * @param statistics
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
}