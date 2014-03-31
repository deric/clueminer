package org.clueminer.clustering.api;

/**
 * A cluster assignment to a data point
 *
 * e.g. "[0] => 1" zero is index of Instance, 1 is cluster number
 *
 * @author Tomas Barton
 */
public interface Assignment {

    /**
     * Returns the cluster id's that a specific data point (at certain index)
     * was assigned to.
     *
     * @return array of assignments
     */
    public int[] membership();

    /**
     * Assign an instance ID to a cluster with given ID
     *
     * @param instanceId
     * @param clusterId
     */
    public void assign(int instanceId, int clusterId);

    /**
     *
     * @param instanceId
     * @return cluster ID to which was instanceId assigned
     */
    public int assigned(int instanceId);

    /**
     * Returns the number of assignments given for this dataset.
     *
     * @return
     */
    public int size();

    /**
     * When creating clustering is useful to know how many clusters we have so
     * far.
     *
     * @return number of distinct assignments
     */
    public int distinct();
}
