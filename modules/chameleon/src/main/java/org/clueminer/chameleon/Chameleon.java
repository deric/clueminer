package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.chameleon.mo.PairMergerMO;
import org.clueminer.chameleon.similarity.BBK1;
import org.clueminer.clustering.algorithm.DBSCAN;
import org.clueminer.clustering.algorithm.DBSCANParamEstim;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.MergeEvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.BisectionFactory;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.partitioning.api.MergerFactory;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.api.PartitioningFactory;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
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
public class Chameleon<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements AgglomerativeClustering<E, C> {

    /**
     * Number of neighbors for each node in k-NN algorithm.
     */
    public static final String K = "k";
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
    public double closenessPriority;

    public static final String INTERCONNECTIVITY_PRIORITY = "interconnectivity_priority";
    @Param(name = Chameleon.INTERCONNECTIVITY_PRIORITY, description = "Priority of merging close clusters")
    public double interconnectivityPriority;

    /**
     * Algorithm for merging clusters
     */
    public static final String MERGER = "merger";

    /**
     * Similarity function used to compute similarity between two clusters
     * during merging.
     */
    public static final String SIM_MEASURE = "similarity_measure";
    @Param(name = Chameleon.SIM_MEASURE, description = "Similarity function used to compute similarity between two clusters")
    private String similarityMeasure;

    /**
     * Method for removing noise
     */
    public static final String NOISE_DETECTION = "noise_detection";
    @Param(name = Chameleon.NOISE_DETECTION, description = "Noise detection method")

    /**
     * Threshold in merger noise detection.
     */
    public static final String INTERNAL_NOISE_THRESHOLD = "internal_noise_threshold";
    @Param(name = Chameleon.INTERNAL_NOISE_THRESHOLD, description = "Threshold in noise detection based on internal properties")

    /**
     * Constant used to multiply external similarity of cluster pairs where one
     * of the clusters contains just one node.
     */
    public static final String INDIVIDUAL_MULTIPLIER = "individual_multiplier";
    @Param(name = Chameleon.INDIVIDUAL_MULTIPLIER, description = "Constant used to multiply external similarity of cluster pairs where one"
           + "of the clusters contains just one node.")
    protected int individualMultiplier;

    private final KNNGraphBuilder knn;

    public static final String GRAPH_STORAGE = "graph_storage";
    @Param(name = Chameleon.GRAPH_STORAGE, description = "Structure for storing graphs")
    private String graphStorage;

    public static final String OBJECTIVE_1 = "mo_objective_1";
    public static final String OBJECTIVE_2 = "mo_objective_2";
    //Noise detection methods
    public static final int NOISE_NONE = 0;
    public static final int NOISE_DBSCAN = 1;
    public static final int NOISE_INTERNAL_PROPERTIES = 2;

    /**
     * 3rd level sorting + objective for computing tree height
     */
    public static final String SORT_OBJECTIVE = "mo_sort";
    /**
     * Number of Pareto fronts
     */
    public static final String NUM_FRONTS = "pareto_fronts";

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

        ArrayList<E> noise = null;
        if (pref.getInt(Chameleon.NOISE_DETECTION, 0) == NOISE_DBSCAN) {
            noise = findNoiseViaDBSCAN(dataset, pref);
            if (noise != null) {
                for (E i : noise) {
                    dataset.remove(i);
                }
            }
        }

        knn.setDistanceMeasure(params.getDistanceMeasure());
        k = pref.getInt(K, -1);
        int datasetK = determineK(dataset);
        maxPartitionSize = pref.getInt(MAX_PARTITION, -1);
        maxPartitionSize = determineMaxPartitionSize(dataset);
        pref.putInt(MAX_PARTITION, maxPartitionSize);
        pref.putInt(K, datasetK);

        graphStorage = pref.get(GRAPH_STORAGE, "org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph");
        //graphStorage = pref.get(GRAPH_STORAGE, "org.clueminer.graph.adjacencyList.AdjListGraph");
        Graph g = null;
        try {
            Class c = Class.forName(graphStorage);
            g = (Graph) c.newInstance();
            g.ensureCapacity(dataset.size());
            g.lookupAdd(dataset);
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
        ArrayList<LinkedList<Node>> partitioningResult = partitioningAlg.partition(maxPartitionSize, g, pref);

        String merger = pref.get(MERGER, "pair merger");
        Merger m = MergerFactory.getInstance().getProvider(merger);
        m.setDistanceMeasure(knn.getDistanceMeasure());
        //Restore noise removed by DBSCAN so it is part of the result
        if (noise != null && pref.getInt(Chameleon.NOISE_DETECTION, 0) == NOISE_DBSCAN) {
            for (E i : noise) {
                dataset.add((E) i);
            }
        }
        MergeEvaluationFactory mef = MergeEvaluationFactory.getInstance();
        if (m instanceof PairMerger) {
            similarityMeasure = pref.get(SIM_MEASURE, BBK1.name);
            MergeEvaluation me = mef.getProvider(similarityMeasure);
            ((PairMerger) m).setMergeEvaluation(me);
        } else if (m instanceof PairMergerMO) {
            PairMergerMO mo = (PairMergerMO) m;
            mo.clearObjectives();
            mo.addObjective(mef.getProvider(pref.get(OBJECTIVE_1)));
            mo.addObjective(mef.getProvider(pref.get(OBJECTIVE_2)));
        }
        noise = m.initialize(partitioningResult, g, bisectionAlg, pref, noise);
        HierarchicalResult result = m.getHierarchy(dataset, pref);
        result.setNoise(noise);
        return result;
    }

    private int determineK(Dataset<E> dataset) {
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

    private int determineMaxPartitionSize(Dataset<E> dataset) {
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

    private ArrayList<E> findNoiseViaDBSCAN(Dataset<E> dataset, Props pref) {
        DBSCANParamEstim<E> estimation = new DBSCANParamEstim<>();
        estimation.estimate(dataset, pref);
        pref.putInt(DBSCAN.MIN_PTS, 4);
        DBSCAN dbScan = new DBSCAN();
        return dbScan.findNoise(dataset, pref);
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
