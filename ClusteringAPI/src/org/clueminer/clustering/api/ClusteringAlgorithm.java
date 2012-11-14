package org.clueminer.clustering.api;

import org.clueminer.dataset.Dataset;
import org.clueminer.distance.DistanceMeasure;
import org.clueminer.instance.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.AlgorithmParameters;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringAlgorithm {

    public String getName();

    /**
     * This method will partition the clustering algorithm on a particular data
     * set. The result will be a Clustering (a set of Datasets) where each data
     * set is a cluster
     *
     * @param dataset
     */
    public Clustering partition(Dataset<Instance> dataset);

    public Clustering partition(Dataset<Instance> dataset, AlgorithmParameters params);

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    public HierarchicalResult hierarchy(Dataset<Instance> dataset, AlgorithmParameters params);
    
    public HierarchicalResult hierarchy(Matrix input, Dataset<Instance> dataset, AlgorithmParameters params);

    public DistanceMeasure getDistanceFunction();

    public void setDistanceFunction(DistanceMeasure dm);
}
