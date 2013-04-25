package org.clueminer.clustering.api;


import java.util.prefs.Preferences;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.AlgorithmParameters;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringAlgorithm {

    public String getName();
    
        /**
     * Clusters the set of rows in the given {@code Matrix} without a specified
     * number of clusters (optional operation).  The set of cluster assignments
     * are returned for each row in the matrix.
     *
     * @param matrix the {@link Matrix} whose row data points are to be
     *        clustered
     * @param props the properties to use for any parameters each clustering
     *        algorithm may need 
     *
     * @return an array of {@link Assignment} instances that indicate zero or
     *         more clusters to which each row belongs.
     */
    public HierarchicalResult cluster(Matrix matrix, Preferences props);
    

    /**
     * This method will partition the clustering algorithm on a particular data
     * set. The result will be a Clustering (a set of Datasets) where each data
     * set is a cluster
     *
     * @param dataset
     */
    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset);

    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset, AlgorithmParameters params);

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, AlgorithmParameters params);
    
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, AlgorithmParameters params);

    public DistanceMeasure getDistanceFunction();

    public void setDistanceFunction(DistanceMeasure dm);
}
