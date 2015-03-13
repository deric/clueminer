package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.partitioning.impl.SpectralBisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Chameleon extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    /**
     * Number of neighbors for each node in k-NN algorithm
     */
    int k;

    /**
     * Whether the partitioning algorithm should consider edge weights
     */
    boolean weightedPartitioning;

    /**
     * Maximum number of nodes in each partition after the execution of the
     * partitioning algorithm
     */
    int maxPartitionSize;

    DistanceMeasure distanceMeasure;

    MergingStrategy mergeStrategy;

    public Chameleon() {
        this(-1, -1, true, MergingStrategy.PAIR);
    }

    public Chameleon(int k) {
        this(k, -1, true, MergingStrategy.PAIR);
    }

    public Chameleon(int k, int maxPartitionSize) {
        this(k, maxPartitionSize, true, MergingStrategy.PAIR);
    }

    public Chameleon(int k, int maxPartitionSize, boolean weightedPartitioning) {
        this(k, maxPartitionSize, weightedPartitioning, MergingStrategy.PAIR);
    }

    public Chameleon(int k, int maxPartitionSize, boolean weightedPartitioning, MergingStrategy mergeStrategy) {
        //checks??
        this.k = k;
        this.maxPartitionSize = maxPartitionSize;
        this.weightedPartitioning = weightedPartitioning;
        this.mergeStrategy = mergeStrategy;
        distanceMeasure = new EuclideanDistance();
    }

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceMeasure = dm;
    }

    @Override
    public String getName() {
        return "Chameleon";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        KNN knn = new KNN(k);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        RecursiveBisection rb = new RecursiveBisection();
        ArrayList<LinkedList<Node>> partitioningResult = rb.partition(maxPartitionSize, g);

        Merger m;

        switch (mergeStrategy) {
            case MULTIPLE:
                m = new MultipleMerger(g);
                break;
            case PAIR:
                m = new PairMerger(g);
                break;
            default:
                m = new MultipleMerger(g);
        }

        //Number of merges will be decided from hierarchical result
        m.merge(partitioningResult, 10);

        return null;

    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
        if (pref != null) {
            AgglParams params = new AgglParams(pref);
            if (!params.clusterRows()) {
                return null;
            }
        }

        KNN knn = new KNN(k);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        RecursiveBisection rb = new RecursiveBisection();
        ArrayList<LinkedList<Node>> partitioningResult = rb.partition(maxPartitionSize, g);

        PairMerger m = new PairMerger(g, new SpectralBisection());

        return m.getHierarchy(partitioningResult, dataset);
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
