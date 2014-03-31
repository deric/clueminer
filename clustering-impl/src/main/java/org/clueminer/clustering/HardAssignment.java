package org.clueminer.clustering;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.clueminer.clustering.api.Assignment;

/**
 * In case of hard assignment each point belong exactly to one cluster.
 *
 * @author Tomas Barton
 */
public class HardAssignment implements Assignment {

    /**
     * The array holding the single assignment.
     */
    private int[] assignments;
    /**
     * Fast hash set for counting distinct cluster numbers
     */
    private final IntSet clusters = new IntOpenHashSet();

    /**
     * Creates a new {@link HardAssignment} the data point is not assigned to
     * any cluster.
     */
    public HardAssignment() {
        assignments = new int[0];
    }

    public HardAssignment(int capacity) {
        assignments = new int[capacity];
    }

    /**
     * Create assignments from an array
     *
     * @param membership
     */
    public HardAssignment(int[] membership) {
        assignments = membership;
        for (int i : assignments) {
            if (!clusters.contains(i)) {
                clusters.add(i);
            }
        }
    }

    @Override
    public int[] membership() {
        return assignments;
    }

    /**
     * We don't track number of assigned values, this is just current array
     * capacity
     *
     * @return current capacity of assignments
     */
    @Override
    public int size() {
        return assignments.length;
    }

    @Override
    public int distinct() {
        return clusters.size();
    }

    @Override
    public void assign(int instanceId, int clusterId) {
        ensureCapacity(instanceId);
        assignments[instanceId] = clusterId;
        if (!clusters.contains(clusterId)) {
            clusters.add(clusterId);
        }
    }

    /**
     * Ensures allocation of big enough array for storing integer values
     *
     * @param capacity
     */
    private void ensureCapacity(int capacity) {
        if (capacity >= assignments.length) {
            //golden ratio
            int newCapacity = (int) (capacity * 1.618);
            if (newCapacity <= capacity) {
                //for small integer values
                newCapacity = capacity + 1;
            }
            int[] newArray = new int[newCapacity];
            System.arraycopy(assignments, 0, newArray, 0, assignments.length);
            assignments = newArray;
        }
    }

    /**
     * Return cluster ID where instanceId belongs
     *
     * @param instanceId
     * @return
     */
    @Override
    public int assigned(int instanceId) {
        return assignments[instanceId];
    }
}
