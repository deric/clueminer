package org.clueminer.clustering.api;

/**
 * A cluster assignment to a data point
 *
 * @author Tomas Barton
 */
public interface Assignment {

    /**
     * Returns the cluster id's that a specific data point was assigned to.
     */
    public int[] assignments();

    /**
     * Returns the number of assignments given for this data point.
     */
    public int length();
}
