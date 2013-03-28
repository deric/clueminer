package org.clueminer.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Assignments;
import org.clueminer.math.DoubleVector;
import org.clueminer.math.Matrix;
import org.clueminer.math.SparseDoubleVector;
import org.clueminer.math.SparseMatrix;
import org.clueminer.math.SparseVector;
import org.clueminer.math.Vector;
import org.clueminer.math.impl.CompactSparseVector;
import org.clueminer.math.impl.DenseVector;
import org.clueminer.math.impl.ScaledDoubleVector;
import org.clueminer.math.impl.ScaledSparseDoubleVector;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class AssigmentsImpl implements Assignments {

    /**
     * The {@link Assignment}s made for each data point.
     */
    private Assignment[] assignments;
    /**
     * The number of clusters found from a particular algorithm.
     */
    private int numClusters;
    /**
     * The {@link Matrix} of data points that these {@link Assignments} link to.
     */
    private Matrix matrix;
    private int[] counts;

    /**
     * Creates a new {@link Assignments} instance that can hold up to {@code
     * numAssignments} {@link Assignment}s. This assumes that the data matrix
     * will not be accessible. Calls to {@link #getCentroids} will fail when
     * using this constructor.
     */
    public AssigmentsImpl(int numClusters, int numAssignments) {
        this(numClusters, numAssignments, null);
    }

    /**
     * Creates a new {@link Assignments} instance that can hold up to {@code
     * numAssignments} {@link Assignment}s.
     */
    public AssigmentsImpl(int numClusters, int numAssignments, Matrix matrix) {
        this.numClusters = numClusters;
        this.matrix = matrix;
        assignments = new Assignment[numAssignments];
    }

    /**
     * Creates a new {@link Assignments} instance that takes ownership of the
     * {@code initialAssignments} array. This assumes that the data matrix will
     * not be accessible. Calls to {@link #getCentroids} will fail when using
     * this constructor.
     */
    public AssigmentsImpl(int numClusters, Assignment[] initialAssignments) {
        this(numClusters, initialAssignments, null);
    }

    /**
     * Creates a new {@link Assignments} instance that takes ownership of the
     * {@code initialAssignments} array.
     */
    public AssigmentsImpl(int numClusters, Assignment[] initialAssignments, Matrix matrix) {
        this.numClusters = numClusters;
        this.matrix = matrix;
        assignments = initialAssignments;
    }

    /**
     * Sets {@link Assignment} {@code i} to have value {@code assignment}.
     */
    @Override
    public void set(int i, Assignment assignment) {
        assignments[i] = assignment;
    }

    /**
     * Returns the number of {@link Assignment} objects stored.
     */
    @Override
    public int size() {
        return assignments.length;
    }

    /**
     * Returns an iterator over the {@link Assignment} objects stored.
     */
    @Override
    public Iterator<Assignment> iterator() {
        return Arrays.asList(assignments).iterator();
    }

    /**
     * Returns the {@link Assignment} object at index {@code i}.
     */
    @Override
    public Assignment get(int i) {
        return assignments[i];
    }

    /**
     * Returns the number of clusters.
     */
    @Override
    public int numClusters() {
        return numClusters;
    }

    /**
     * Returns the array of {@link Assignment} objects.
     */
    @Override
    public Assignment[] assignments() {
        return assignments;
    }

    /**
     * Returns the data point indices assigned to each cluster.
     */
    @Override
    public List<Set<Integer>> clusters() {
        List<Set<Integer>> clusters = new ArrayList<Set<Integer>>();
        for (int c = 0; c < numClusters; ++c) {
            clusters.add(new HashSet<Integer>());
        }
        for (int i = 0; i < assignments.length; ++i) {
            for (int k : assignments[i].assignments()) {
                clusters.get(k).add(i);
            }
        }
        return clusters;
    }

    /**
     * Returns an array of dense centroid vectors of each discovered cluster
     * which are scaled according the the number of data points asisgned to that
     * cluster. Note that this method assumes that the original {@link Matrix}
     * holding the data points contains rows of feature vectors.
     */
    @Override
    public Vector<Double>[] getCentroids() {
        if (matrix == null) {
            throw new IllegalArgumentException("The data matrix was not passed to Assignments.");
        }
        // Initialzie the centroid vectors and the cluster sizes.
        DoubleVector[] centroids = new DoubleVector[numClusters];
        counts = new int[numClusters];
        for (int c = 0; c < numClusters; ++c) {
            centroids[c] = new DenseVector(matrix.columnsCount());
        }

        // For each initial assignment, add the vector to it's centroid and
        // increase the size of the cluster.
        int row = 0;
        for (Assignment assignment : assignments) {
            if (assignment.length() != 0) {
                counts[assignment.assignments()[0]]++;
                DoubleVector centroid = centroids[assignment.assignments()[0]];
                centroid.add(matrix.getRowVector(row));
            }
            row++;
        }

        // Scale any non empty clusters by their size.
        for (int c = 0; c < numClusters; ++c) {
            if (counts[c] != 0) {
                centroids[c] = new ScaledDoubleVector(centroids[c], 1d / counts[c]);
            }
        }

        return centroids;
    }

    /**
     * Returns an array of sparse centroid vectors of each discovered cluster
     * which are scaled according the the number of data points assigned to that
     * cluster. This assumes that the original {@link Matrix} is sparse. Note
     * that this method assumes that the original {@link Matrix} holding the
     * data points contains rows of feature vectors.
     */
    @Override
    public SparseVector<Double>[] getSparseCentroids() {
        if (matrix == null) {
            throw new IllegalArgumentException("The data matrix was not passed to Assignments.");
        }

        SparseMatrix sm = (SparseMatrix) matrix;

        // Initialzie the centroid vectors and the cluster sizes.
        SparseDoubleVector[] centroids = new SparseDoubleVector[numClusters];

        // If for some odd reason, no clusters were found, return no centroids.
        if (numClusters == 0) {
            return centroids;
        }

        counts = new int[numClusters];
        for (int c = 0; c < numClusters; ++c) {
            centroids[c] = new CompactSparseVector(matrix.columnsCount());
        }

        // For each initial assignment, add the vector to it's centroid and
        // increase the size of the cluster.
        int row = 0;
        for (Assignment assignment : assignments) {
            if (assignment.length() != 0 && assignment.assignments()[0] != -1) {
                counts[assignment.assignments()[0]]++;
                SparseDoubleVector centroid = centroids[assignment.assignments()[0]];
                centroid.add(sm.getRowVector(row));
            }
            row++;
        }

        // Scale any non empty clusters by their size.
        for (int c = 0; c < numClusters; ++c) {
            if (counts[c] != 0) {
                centroids[c] = new ScaledSparseDoubleVector(centroids[c], 1d / counts[c]);
            }
        }

        return centroids;
    }

    @Override
    public int[] clusterSizes() {
        return counts;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("assignments: ");
        List<Set<Integer>> clust = this.clusters();
        for (int i = 0; i < clust.size(); i++) {
            sb.append("cluster ").append(i).append(clust.get(i));
        }
        return sb.toString();        
    }
}
