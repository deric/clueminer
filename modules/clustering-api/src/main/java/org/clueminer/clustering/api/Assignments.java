package org.clueminer.clustering.api;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.clueminer.math.SparseVector;

/**
 * The return value for all {@link Clustering} implementations.  This class
 * records the number of clusters created, the assignments for each value, and
 * helper methods for constructing the centroids of a cluster.
 *
 * @author Keith Stevens, Tomas Barton
 */
public interface Assignments extends Iterable<Assignment> {

    /**
     * Sets {@link Assignment} {@code i} to have value {@code assignment}.
     */
    public void set(int i, Assignment assignment);

    /**
     * Returns the number of {@link Assignment} objects stored.
     */
    public int size();

    /**
     * Returns an iterator over the {@link Assignment} objects stored.
     */
    @Override
    public Iterator<Assignment> iterator();
    /**
     * Returns the {@link Assignment} object at index {@code i}.
     */
    public Assignment get(int i);

    /**
     * Returns the number of clusters.
     */
    public int numClusters();

    /**
     * Returns the array of {@link Assignment} objects.
     */
    public Assignment[] assignments();
    
    /**
     * Returns the data point indices assigned to each cluster.
     */
    public List<Set<Integer>> clusters();

    /**
     * Returns an array of dense centroid vectors of each discovered cluster
     * which are scaled according the the number of data points asisgned to that
     * cluster.  Note that this method assumes that the original {@link Matrix}
     * holding the data points contains rows of feature vectors.  
     */
    public Vector<Double>[] getCentroids();
    
    /**
     * Returns an array of sparse centroid vectors of each discovered cluster
     * which are scaled according the the number of data points asisgned to that
     * cluster.  This assumes that the original {@link Matrix} is sparse.  Note
     * that this method assumes that the original {@link Matrix} holding the
     * data points contains rows of feature vectors.  
     */
    public SparseVector<Double>[] getSparseCentroids();

    public int[] clusterSizes();
}
