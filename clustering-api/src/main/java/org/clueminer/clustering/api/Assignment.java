package org.clueminer.clustering.api;

/**
 * A cluster assignment to a data point
 *
 * @author Tomas Barton
 */
public interface Assignment {

    /**
     * Returns the cluster id's that a specific data point was assigned to.
     *
     * @return
     */
    public int[] assignments();

    /**
     * Returns the number of assignments given for this data point.
     *
     * @return
     */
    public int length();
}
