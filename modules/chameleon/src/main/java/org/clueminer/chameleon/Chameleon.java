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
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.MergeEvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.BisectionFactory;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.api.PartitioningFactory;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Chameleon is a hierarchical graph clustering algorithm.
 *
 * Karypis, George, Eui-Hong Han, and Vipin Kumar. "Chameleon: Hierarchical
 * clustering using dynamic modeling." Computer 32.8 (1999): 68-75.
 *
 * @author Tomas Bruna
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Chameleon<E extends Instance, C extends Cluster<E>> extends AbstractClusteringAlgorithm<E, C> implements AgglomerativeClustering<E, C> {

    public static final String K = "k";

    /**
     * Number of neighbors for each node in k-NN algorithm.
     */
    @Param(name = Chameleon.K, description = "Number of neighbors for each node in k-NN algorithm", required = false)
    private int k;

    /**
     * Maximum number of nodes in each partition after the execution of the
     * partitioning algorithm.
     */
    public static final String MAX_PARTITION = "max_partition_size";

    @Param(name = Chameleon.MAX_PARTITION, description = "Maximum number of nodes in each partition after the execution of the partitioning algorithm")
    private int maxPartitionSize;

    /**
     * Bisection algorithm used in partitioning and merging.
     */
    public static final String BISECTION = "bisection";
    @Param(name = Chameleon.BISECTION, description = "Bisection algorithm")
    private String bisection;

    /**
     * Partitioning algorithm.
     */
    public static final String PARTITIONING = "partitioning";
    @Param(name = Chameleon.PARTITIONING, description = "Partitioning algorithm")
    private String partitioning;

    /**
     * If bigger than 1, algorithm gives a higher importance to the relative
     * closeness of clusters during merging, otherwise, if lesser than 1, to
     * interconnectivity.
     */
    public static final String CLOSENESS_PRIORITY = "closeness_priority";

    @Param(name = Chameleon.CLOSENESS_PRIORITY, description = "Priority of merging close clusters")
    private double closenessPriority;

    /**
     * Similarity function used to compute similarity between two clusters
     * during merging.
     */
    public static final String SIM_MEASURE = "similarity_measure";

    @Param(name = Chameleon.SIM_MEASURE, description = "Similarity function used to compute similarity between two clusters")
    private String similarityMeasure;

    private RecursiveBisection recursiveBisection;

    private final KNNGraphBuilder knn;

    public static final String GRAPH_STORAGE = "graph_storage";
    @Param(name = Chameleon.GRAPH_STORAGE, description = "Structure for storing graphs")
    private String graphStorage;

    public Chameleon() {
        knn = new KNNGraphBuilder();
    }

    @Override
    public String getName() {
        return "Chameleon";
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props pref) {
        HierarchicalResult res = hierarchy(dataset, pref);
        return res.getClustering();
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<E> dataset, Props pref) {
        AgglParams params = new AgglParams(pref);
        if (params.clusterColumns()) {
            // throw new RuntimeException("Chameleon cannot cluster attributes");
            System.out.println("Chameleon cannot cluster attributes");
            return null;
        }

        knn.setDistanceMeasure(params.getDistanceMeasure());
        k = pref.getInt(K, -1);
        int datasetK = determineK(dataset);
        maxPartitionSize = pref.getInt(MAX_PARTITION, -1);
        maxPartitionSize = determineMaxPartitionSize(dataset);

        graphStorage = pref.get(GRAPH_STORAGE, "org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph");
        //graphStorage = pref.get(GRAPH_STORAGE, "org.clueminer.graph.adjacencyList.AdjListGraph");
        Graph g = null;
        try {
            Class c = Class.forName(graphStorage);
            g = (Graph) c.newInstance();
            g.ensureCapacity(dataset.size());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (g == null) {
            throw new RuntimeException("failed to initialize graph: " + graphStorage);
        }
        g = knn.getNeighborGraph(dataset, g, datasetK);

        //bisection = pref.get(BISECTION, "Kernighan-Lin");
        bisection = pref.get(BISECTION, "Fiduccia-Mattheyses");
        Bisection bisectionAlg = BisectionFactory.getInstance().getProvider(bisection);
        if (bisectionAlg instanceof FiducciaMattheyses) {
            FiducciaMattheyses fm = (FiducciaMattheyses) bisectionAlg;
            fm.setIterationLimit(pref.getInt(FiducciaMattheyses.ITERATIONS, 20));
        }

        partitioning = pref.get(PARTITIONING, "Recursive bisection");
        Partitioning partitioningAlg = PartitioningFactory.getInstance().getProvider(partitioning);
        partitioningAlg.setBisection(bisectionAlg);
        ArrayList<LinkedList<Node>> partitioningResult = partitioningAlg.partition(maxPartitionSize, g);

        PairMerger m;
        closenessPriority = pref.getDouble(CLOSENESS_PRIORITY, 2.0);

        similarityMeasure = pref.get(SIM_MEASURE, SimilarityMeasure.IMPROVED);
        MergeEvaluation me = MergeEvaluationFactory.getInstance().getProvider(similarityMeasure);
        //TODO this is ugly, we have to move it to different interface
        m = (PairMerger) me;
        m.setGraph(g);
        m.setBisection(bisectionAlg);

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
            } else if ((dataset.size() < 2000)) {
                return dataset.size() / 100;
            } else {
                return dataset.size() / 200;
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
