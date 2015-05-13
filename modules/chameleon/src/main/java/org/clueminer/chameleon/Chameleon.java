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
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.BisectionFactory;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Chameleon extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    /**
     * Number of neighbors for each node in k-NN algorithm.
     */
    private int k;

    /**
     * Maximum number of nodes in each partition after the execution of the
     * partitioning algorithm.
     */
    private int maxPartitionSize;

    /**
     * Bisection algorithm used in partitioning and merging.
     */
    private Bisection bisection;

    /**
     * If bigger than 1, algorithm gives a higher importance to the relative
     * closeness of clusters during merging, otherwise, if lesser than 1, to
     * interconnectivity.
     */
    private double closenessPriority;

    /**
     * Similarity function used to compute similarity between two clusters
     * during merging.
     */
    private SimilarityMeasure similarityMeasure;

    private final RecursiveBisection recursiveBisection;

    private final KNNGraphBuilder knn;

    public Chameleon() {
        k = -1;
        maxPartitionSize = -1;
        bisection = BisectionFactory.getInstance().getProvider("Fiduccia-Mattheyses");
        closenessPriority = 2;
        similarityMeasure = SimilarityMeasure.IMPROVED;
        recursiveBisection = new RecursiveBisection(bisection);
        knn = new KNNGraphBuilder();
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setMaxPartitionSize(int size) {
        maxPartitionSize = size;
    }

    public void setClosenessPriority(double priority) {
        closenessPriority = priority;
    }

    public void setImprovedMeasure() {
        similarityMeasure = SimilarityMeasure.IMPROVED;
    }

    public void setStandardMeasure() {
        similarityMeasure = SimilarityMeasure.STANDARD;
    }

    public void setBisection(Bisection bisection) {
        this.bisection = bisection;
        recursiveBisection.setBisection(bisection);
    }

    @Override
    public String getName() {
        return "Chameleon";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
        AgglParams params = new AgglParams(pref);
        if (params.clusterColumns()) {
            // throw new RuntimeException("Chameleon cannot cluster attributes");
            System.out.println("Chameleon cannot cluster attributes");
            return null;
        }

        knn.setDistanceMeasure(params.getDistanceMeasure());
        int datasetK = determineK(dataset);
        int datasetMaxPSize = determineMaxPartitionSize(dataset);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g, datasetK);

        ArrayList<LinkedList<Node>> partitioningResult = recursiveBisection.partition(datasetMaxPSize, g);

        PairMerger m;
        if (similarityMeasure == SimilarityMeasure.IMPROVED) {
            m = new ImprovedSimilarity(g, bisection, closenessPriority);
        } else if (similarityMeasure == SimilarityMeasure.STANDARD) {
            m = new StandardSimilarity(g, bisection, closenessPriority);
        } else {
            throw new IllegalArgumentException("Unknown similarity measure.");
        }
        return m.getHierarchy(partitioningResult, dataset, pref);
    }

    private int determineK(Dataset<? extends Instance> dataset) {
        if (k == -1) {
            if (dataset.size() < 500) {
                return (int) (Math.log(dataset.size()) / Math.log(2));
            } else {
                return (int) (Math.log(dataset.size()) / Math.log(2)) * 2;
            }
        } else {
            return k;
        }
    }

    private int determineMaxPartitionSize(Dataset<? extends Instance> dataset) {
        if (maxPartitionSize == -1) {
            if (dataset.size() < 500) {
                return 5;
            } else {
                return 10;
            }
        } else {
            return maxPartitionSize;
        }
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
