package org.clueminer.clustering;

import org.clueminer.clustering.api.Assignment;

/**
 *
 * @author Tomas Barton
 */
public class HardAssignment implements Assignment {

    /**
     * The array holding the single assignment.
     */
    private final int[] assignments;

    /**
     * Creates a new {@link HardAssignment} the data point is not assigned to
     * any cluster.
     */
    public HardAssignment() {
        assignments = new int[0];
    }

    /**
     * Creates a new {@link HardAssignment} where the data point is assigned to
     * the specified cluster value.
     */
    public HardAssignment(int assignment) {
        assignments = new int[1];
        assignments[0] = assignment;
    }

    @Override
    public int[] assignments() {
        return assignments;
    }

    @Override
    public int length() {
        return assignments.length;
    }
}
