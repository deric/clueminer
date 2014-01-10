/*
 * Copyright 2009 David Jurgens
 *
 * This file is part of the S-Space package and is covered under the terms and
 * conditions therein.
 *
 * The S-Space package is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation and distributed hereunder to you.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND NO REPRESENTATIONS OR WARRANTIES,
 * EXPRESS OR IMPLIED ARE MADE.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, WE MAKE
 * NO REPRESENTATIONS OR WARRANTIES OF MERCHANT- ABILITY OR FITNESS FOR ANY
 * PARTICULAR PURPOSE OR THAT THE USE OF THE LICENSED SOFTWARE OR DOCUMENTATION
 * WILL NOT INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER
 * RIGHTS.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.algorithm;

import org.clueminer.math.matrix.JMatrix;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.clueminer.clustering.Pairing;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.LinkageFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.exec.WorkQueue;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.utils.AlgorithmParameters;

/**
 * A utility class for performing <a
 * href="http://en.wikipedia.org/wiki/Cluster_analysis#Agglomerative_hierarchical_clustering">Hierarchical
 * Agglomerative Clustering</a> on matrix data in a file.
 *
 * </p> This class provides static accessors to several variations of
 * agglomerative clustering and conforms to the {@link Clustering} interface,
 * which allows this method to be used in place of other clustering algorithms.
 *
 * <p>
 * In addition to clustering, this implementation also exposes the ability
 * to view the iterative bottom-up merge through the {@link
 * buildDendrogram(Matrix,ClusterLinkage,SimType) buildDendogram} methods. These
 * methods return a series of {@link Merge} operations that can be used to
 * construct a <a href="http://en.wikipedia.org/wiki/Dendrogram">dendrogram</a>
 * and see the partial clustering at any point during the agglomerative merging
 * process. For example, to view the clustering solution after four steps, the
 * following code might be used:
 *
 * <pre name="code" class="java:nocontrols:nogutter"> Matrix matrix;
 * List&lt;Merge&gt; merges = buildDendogram(matrix, ...); List&lt;Merge&gt;
 * fourMergeSteps = merges.subList(0, 4); MultiMap&lt;Integer,Integer&gt;
 * clusterToRows = new HashMultiMap&lt;Integer,Integer&gt;(); for (int i = 0; i
 * &lt; matrix.rows(); ++i) clusterToElements.put(i, i);
 *
 * for (Merge m : fourMergeSteps) {
 * clusterToElements.putMulti(m.remainingCluster(),
 * clusterToElements.remove(m.mergedCluster())); }
 * </pre>
 *
 * The resulting {@link edu.ucla.sspace.util.MultiMap} {@code clusterToRows}
 * contains the mapping from each cluster to the rows that are a part of it.
 *
 * <p>
 * <i>Implementation Note:</i> The current version runs in O(n<sup>3</sup>)
 * worst case time for the number of rows in the matrix. While O(n<sup>2</sup> *
 * log(n)) methods exist, these require storing similarity comparisons in a
 * priority queue, which has a substantially higher memory overhead. Therefore,
 * this implementation has opted for a more expensive running time in order to
 * be able to process larger matrices.
 *
 * <p>
 * When using the {@link Clustering#cluster(Matrix,Properties)} interface,
 * this class supports the following properties for controlling the clustering.
 *
 * <dl style="margin-left: 1em"> <dt> <i>Property:</i>
 * <code><b>{@value #MIN_CLUSTER_SIMILARITY} </b></code> <br> <i>Default:</i>
 * unset
 *
 * <dd style="padding-top: .5em"> This property specifies the cluster similarity
 * threshold at which two clusters are merged together. Merging will continue
 * until either all clusters have similarities below this threshold or the
 * number of desired clusters has been reached. This property provides an
 * alternative to the num of clusters property for deciding when to stop
 * agglomeratively merging clusters. Both properties cannot be specified at the
 * same time. </p>
 *
 * <dt> <i>Property:</i>
 * <code><b>{@value #CLUSTER_LINKAGE} </b></code> <br> <i>Default:</i>
 * {@value #DEFAULT_CLUSTER_LINKAGE}
 *
 * <dd style="padding-top: .5em"> This property specifies the {@link
 *       ClusterLinkage} to use when computing cluster similarity. </p>
 *
 * <dt> <i>Property:</i>
 * <code><b>{@value #DISTANCE_FUNCTION} </b></code> <br> <i>Default:</i>
 * {@link SimType#COSINE COSINE}
 *
 * <dd style="padding-top: .5em"> This property specifies the name of {@link
 *       SimType} to use when computing the similarity of two data points. </p>
 *
 * <dt> <i>Property:</i>
 * <code><b>{@value #NUM_CLUSTERS} </b></code> <br> <i>Default:</i> unset
 *
 * <dd style="padding-top: .5em"> This property specifies the number of clusters
 * to generate from the data. Clusters are agglomeratively merged until the
 * specified number of clusters is reached. This property provides an
 * alternative to the cluster similarity property for deciding when to stop
 * agglomeratively merging clusters. Both properties cannot be specified at the
 * same time. </p>
 *
 * </dl>
 *
 * @author David Jurgens
 * @author Tomas Barton
 */
public class HierarchicalAgglomerativeClustering extends AbstractClusteringAlgorithm implements ClusteringAlgorithm {

    private static final String name = "Hierarchical Clustering";
    /**
     * A prefix for specifying properties.
     */
    public static final String PREF_PREFIX
            = "org.clueminer.clustering.algorithm.HierarchicalAgglomerativeClustering";
    /**
     * The property for specifying the cluster similarity threshold.
     */
    public static final String MIN_CLUSTER_SIMILARITY = PREF_PREFIX + ".clusterThreshold";
    /**
     * The property for specifying the cluster linkage to use.
     */
    public static final String CLUSTER_LINKAGE = PREF_PREFIX + ".clusterLinkage";
    /**
     * The property for specifying the similarity function to use.
     */
    public static final String DISTANCE_FUNCTION = PREF_PREFIX + ".distanceMeasure";
    /**
     * The property for specifying the similarity function to use.
     */
    public static final String NUM_CLUSTERS = PREF_PREFIX + ".numClusters";
    /**
     * The default similarity threshold to use.
     */
    private static final double DEFAULT_MIN_CLUSTER_SIMILARITY = -1.0;
    /**
     * The default linkage method to use.
     */
    public static final String DEFAULT_CLUSTER_LINKAGE = "Single Linkage";
    /**
     * The default similarity function to use.
     */
    private static final String DEFAULT_DISTANCE_FUNCTION = "Euclidean";
    /**
     * The logger to which clustering status updates will be written.
     */
    private static final Logger LOGGER = Logger.getLogger(HierarchicalAgglomerativeClustering.class.getName());
    /**
     * The work used by all HAC instances to perform multi-threaded operations.
     */
    private final WorkQueue workQueue;
    private ClusterLinkage linkage;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset, AlgorithmParameters params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, AlgorithmParameters params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, AlgorithmParameters params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HierarchicalAgglomerativeClustering() {
        this.workQueue = WorkQueue.getWorkQueue();
    }

    public ClusterLinkage getLinkage() {
        return linkage;
    }

    public void setLinkage(ClusterLinkage linkage) {
        this.linkage = linkage;
    }

    private void parseLinkage(Preferences props) {
        String simFuncProp = props.get(DISTANCE_FUNCTION, DEFAULT_DISTANCE_FUNCTION);
        setDistanceFunction(DistanceFactory.getDefault().getProvider(simFuncProp));

        String linkageProp = props.get(CLUSTER_LINKAGE, DEFAULT_CLUSTER_LINKAGE);
        setLinkage(LinkageFactory.getDefault().getProvider(linkageProp));
        getLinkage().setDistanceMeasure(distanceMeasure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HierarchicalResult cluster(Matrix matrix, Preferences props) {
        parseLinkage(props);

        double minSimProp = props.getDouble(MIN_CLUSTER_SIMILARITY, DEFAULT_MIN_CLUSTER_SIMILARITY);
        String numClustProp = props.get(NUM_CLUSTERS, null);

        if (minSimProp == Double.NaN && numClustProp == null) {
            throw new IllegalArgumentException(
                    "This class requires either a specified number of clusters or "
                    + "a minimum cluster similarity threshold in order to partition "
                    + "throw rows of the input.  Either needs to be provided as a "
                    + "property");
        } else if (minSimProp != Double.NaN && numClustProp != null) {
            throw new IllegalArgumentException("Cannot specify both a fixed number of"
                    + " clusters AND a minimum cluster similarity as input properties");
        } else if (minSimProp != Double.NaN) {
            HierarchicalResult res = cluster(matrix, minSimProp, linkage, distanceMeasure, -1);
            res.setInputData(matrix);
            res.setNumClusters(-1);
            return res;
        } else {
            return cluster(matrix, -1, props);
        }
    }

    /**
     * {@inheritDoc} The value of the {@code numClusters} parameter will
     * override the {@value #NUM_CLUSTERS} if it was specified.
     *
     * @param data
     * @param numClusters
     * @param props
     * @return
     */
    public HierarchicalResult cluster(Matrix data, int numClusters, Preferences props) {
        if (linkage == null || distanceMeasure == null) {
            parseLinkage(props);
        }

        double clustSimThreshold = props.getDouble(MIN_CLUSTER_SIMILARITY, DEFAULT_MIN_CLUSTER_SIMILARITY);

        HierarchicalResult res = cluster(data, clustSimThreshold, linkage, distanceMeasure, numClusters);
        res.setInputData(data);
        res.setNumClusters(-1);
        return res;
    }

    /**
     * Clusters all rows in the matrix using the specified cluster similarity
     * measure for comparison and stopping when the number of clusters is equal
     * to the specified number.
     *
     * @param m           a matrix whose rows are to be clustered
     * @param numClusters the number of clusters into which the matrix should
     *                    divided
     * @param linkage     the method to use for computing the similarity of two
     *                    clusters
     *
     * @return an array where each element corresponds to a row and the value is
     *         the cluster number to which that row was assigned. Cluster numbers will
     *         start at 0 and increase.
     */
    public static int[] partitionRows(Matrix m, int numClusters, ClusterLinkage linkage, DistanceMeasure similarityFunction) {
        return cluster(m, -1, linkage, similarityFunction, numClusters).getMapping();
    }

    /**
     * Clusters all rows in the matrix using the specified cluster similarity
     * measure for comparison and threshold for when to stop clustering.
     * Clusters will be repeatedly merged until the highest cluster similarity
     * is below the threshold.
     *
     * @param m                          a matrix whose rows are to be clustered
     * @param clusterSimilarityThreshold the threshold to use when deciding
     *                                   whether two clusters should be merged. If the similarity of the clusters
     *                                   is below this threshold, they will not be merged and the clustering
     *                                   process will be stopped.
     * @param linkage                    the method to use for computing the
     *                                   similarity of two
     *                                   clusters
     *
     * @return an array where each element corresponds to a row and the value is
     *         the cluster number to which that row was assigned. Cluster numbers will
     *         start at 0 and increase.
     */
    @SuppressWarnings("unchecked")
    public static int[] clusterRows(Matrix m, double clusterSimilarityThreshold, ClusterLinkage linkage, DistanceMeasure similarityFunction) {
        return cluster(m, clusterSimilarityThreshold, linkage, similarityFunction, -1).getMapping();
    }

    /**
     *
     *
     * @param m                          a matrix whose rows are to be clustered
     * @param clusterSimilarityThreshold the optional parameter for specifying
     *                                   the minimum inter-cluster similarity to use when deciding whether two
     *                                   clusters should be merged. If {@code maxNumberOfClusters} is positive,
     *                                   this value is discarded in order to cluster to a fixed number. Otherwise
     *                                   all clusters will be merged until the minimum distance is less than this
     *                                   threshold.
     * @param linkage                    the method to use for computing the
     *                                   similarity of two
     *                                   clusters
     * @param similarityFunction
     * @param maxNumberOfClusters        an optional parameter to specify the
     *                                   maximum
     *                                   number of clusters to have. If this value is non-positive, clusters will
     *                                   be merged until the inter-cluster similarity is below the threshold,
     *                                   otherwise; if the value is positive, clusters are merged until the
     *                                   desired number of clusters has been reached.
     *
     * @return an array where each element corresponds to a row and the value is
     *         the cluster number to which that row was assigned. Cluster numbers will
     *         start at 0 and increase.
     */
    public static HierarchicalResult cluster(Matrix m, double clusterSimilarityThreshold,
            ClusterLinkage linkage, DistanceMeasure similarityFunction, int maxNumberOfClusters) {
        LOGGER.log(Level.INFO, "Generating similarity matrix for {0} data points", m.rowsCount());
        Matrix similarityMatrix = computeRowSimilarityMatrix(m, similarityFunction);
        //similarityMatrix.print(5, 2);
        similarityMatrix.printLower(5, 2);
        return clusterSimilarityMatrix(similarityMatrix, clusterSimilarityThreshold, linkage, maxNumberOfClusters);
    }

    public static HierarchicalResult clusterSimilarityMatrix(Matrix similarityMatrix,
            double clusterSimilarityThreshold, ClusterLinkage linkage, int maxNumberOfClusters) {

        int rows = similarityMatrix.rowsCount();
        HierarchicalResult result = new HClustResult();
        result.setSimilarityMatrix(similarityMatrix);
        //Dump.matrix(similarityMatrix.getArray(), "similarity", 2);

        // Create the initial set of clusters where each row is originally in
        // its own cluster
        Map<Integer, Set<Integer>> assignments = generateInitialAssignment(rows);
        LOGGER.log(Level.INFO, "Calculating initial inter-cluster similarity using {0}", linkage.getName());
        // Generate the initial set of cluster pairings based on the highest
        // similarity.  This mapping will be update as the number of clusters
        // are reduced, where merging a cluster will causes all the pairings
        // pointing to it constinuents recalculated.
        Map<Integer, Pairing> clusterSimilarities = new HashMap<Integer, Pairing>();
        for (Integer clusterId : assignments.keySet()) {
            clusterSimilarities.put(clusterId, findMostSimilar(assignments, clusterId, linkage, similarityMatrix));
        }

        LOGGER.info("Assigning clusters");

        // Keep track of which ID is available for the new, merged cluster
        int nextClusterId = rows;

        // While we still have more clusters than the maximum number loop.  Note
        // that if the maximum was set to negative, this condition will always
        // be true and the inner loop check for inter-cluster similarity will
        // break out of this loop
        System.out.println("size: " + assignments.size() + " >? max clusters: " + maxNumberOfClusters);
        while (assignments.size() > maxNumberOfClusters) {
            if (assignments != null) {
                System.out.println("assign size: " + assignments.toString());
            }
            // Find a row that has yet to be clustered by searching for the pair
            // that is most similar
            int cluster1index = 0;
            int cluster2index = 0;
            double highestSimilarity = -1;

            // Find the row with the highest similarity to another
            for (Map.Entry<Integer, Pairing> e : clusterSimilarities.entrySet()) {

                Pairing p = e.getValue();
                Integer i = e.getKey();
                Integer j = p.pairedIndex;
                if (p.similarity > highestSimilarity) {
                    cluster1index = i;
                    cluster2index = j;
                    highestSimilarity = p.similarity;
                }
            }
            System.out.println("clusters: " + assignments.size() + ", max clusters: " + maxNumberOfClusters);
            LOGGER.log(Level.INFO, "clusters: {0}, max clusters: {1}, highest sim: {2}, treshold: {3}",
                       new Object[]{assignments.size(), maxNumberOfClusters, highestSimilarity, clusterSimilarityThreshold});
            // If the similarity of the two most similar clusters falls below
            // the threshold, then the final set of clusters has been
            // determined.
            if (maxNumberOfClusters < 1 && highestSimilarity < clusterSimilarityThreshold) {
                break;
            }

            // Assign the merged cluster a new ID, which lets us track any
            // pairings to the original clusters that may need to be
            // recalculated
            int newClusterId = nextClusterId++;
            //System.out.println("new cluster id: "+newClusterId);
            Set<Integer> cluster1 = assignments.get(cluster1index);
            Set<Integer> cluster2 = assignments.get(cluster2index);

            LOGGER.log(Level.FINE, "Merged cluster {0} with {1}",
                       new Object[]{cluster1, cluster2});

            // Update the cluster assignments, adding in the new cluster and
            // remove all references to the two merged clusters.
            cluster1.addAll(cluster2);
            assignments.put(newClusterId, cluster1);
            assignments.remove(cluster1index);
            assignments.remove(cluster2index);
            clusterSimilarities.remove(cluster1index);
            clusterSimilarities.remove(cluster2index);

            // Local state variables to use while recalculating the similarities
            double mostSimilarToMerged = -1;
            Integer mostSimilarToMergedId = null;

            // Check whether we have just merged the last two clusters, in which
            // case the similarity recalculation is unnecessary
            if (clusterSimilarities.isEmpty()) {
                break;
            }

            // Recalculate the inter-cluster similarity of a cluster that was
            // paired with either of these two (i.e. was most similar to one of
            // them before the merge).  At the same time, calculate the
            // most-similar to the newly merged cluster
            for (Map.Entry<Integer, Pairing> e : clusterSimilarities.entrySet()) {

                Integer clusterId = e.getKey();

                // First, calculate the similarity between this cluster and the
                // newly merged cluster
                double simToNewCluster = linkage.similarity(similarityMatrix, cluster1, assignments.get(clusterId));
                if (simToNewCluster > mostSimilarToMerged) {
                    mostSimilarToMerged = simToNewCluster;
                    mostSimilarToMergedId = clusterId;
                }

                // Second, if the pair was previously paired with one of the
                // merged clusters, recompute what its most similar is
                Pairing p = e.getValue();
                if (p.pairedIndex == cluster1index || p.pairedIndex == cluster2index) {
                    // Reassign with the new most-similar
                    e.setValue(findMostSimilar(assignments, clusterId, linkage, similarityMatrix));
                }
            }

            // Update the new most similar to the newly-merged cluster
            clusterSimilarities.put(newClusterId,
                                    new Pairing(mostSimilarToMerged,
                                                mostSimilarToMergedId));
        }

        result.setMapping(toAssignArray(assignments, rows));

        return result;
    }

    /*
     // Recalculate the inter-cluster similarity of a cluster in two cases:
     //
     // 1) a cluster that paired with either of these two (i.e. was most
     // similar to one of them before the merge).
     //
     // 2) the most similar cluster to the newly merged cluster
     Collection<Runnable> similarityTasks = new ArrayList<Runnable>();
     // Dump the map's entries into a list so we can partition them among
     // different processing threads.  Although it's a linear operation,
     // this avoids two potential issues: (1) Having to create a new
     // Runnable for each comparison, and (2) Having a large number of
     // concurrent writes trying to update the most-similar value
     // (high-write contention).
     List<Map.Entry<Integer, Pairing>> toPartition =
     new ArrayList<Map.Entry<Integer, Pairing>>(
     clusterSimilarities.entrySet());
     int numThreads = workQueue.numThreads();
     int comparisonsPerThread = toPartition.size() / numThreads;
     final ConcurrentNavigableMap<Double, Integer> mostSimilarMap = new ConcurrentSkipListMap<Double, Integer>();
     final int c1index = cluster1index;
     final int c2index = cluster2index;
     for (int th = 0; th< numThreads ; ++th ) {
     int start = th * comparisonsPerThread;
     int end = Math.min((th + 1) * comparisonsPerThread,
     toPartition.size());
     final List<Map.Entry<Integer, Pairing>> clustersToUpdate =
     toPartition.subList(start, end);

     similarityTasks.add(new Runnable() {
     public void run() {

     // Thread-local state variables to use while
     // recalculating the similarities
     double mostSimilarToMerged = -1;
     Integer mostSimilarToMergedId = null;

     for (Map.Entry<Integer, Pairing> e
     : clustersToUpdate) {

     Integer clusterId = e.getKey();
     Pairing p = e.getValue();

     // Skip self comparisons for the merged
     // clustering
     if (clusterId == c1index) {
     continue;
     }

     // First, calculate the similarity between this
     // cluster and the newly merged cluster
     double simToNewCluster =
     getSimilarity(similarityMatrix, cluster1,
     clusterAssignment.get(clusterId),
     linkage);

     // If this cluster is now the most similar to
     // the newly-merged cluster update its mapping
     if (simToNewCluster > mostSimilarToMerged) {
     mostSimilarToMerged = simToNewCluster;
     mostSimilarToMergedId = clusterId;
     }

     // Second, if the pair was previously paired with
     // one of the merged clusters, recompute what its
     // most similar is
     if (p.pairedIndex == c1index
     || p.pairedIndex == c2index) {
     // Reassign with the new most-similar
     e.setValue(findMostSimilar(
     clusterAssignment,
     clusterId, linkage,
     similarityMatrix));
     }
     }

     // Once all of the clusters for this thread have
     // been processed, update the similarit map.  We do
     // this last to minimize the contention on the map
     mostSimilarMap.put(mostSimilarToMerged,
     mostSimilarToMergedId);
     }
     });
     }

     // Run each thread's comparisons
     workQueue.run (similarityTasks);
     // Collect the results from the similarity map.  The highest
     // similarity should be the largest key in the map, with the
     // clustering as the value.  Note that if there were ties in the
     // highest similarity, the cluster is selected by the last thread,
     // which is still arbitrarily fair.
     Map.Entry<Double, Integer> highest = mostSimilarMap.lastEntry();

     // Update the new most similar to the newly-merged cluster
     clusterSimilarities.put (cluster1index,


     new Pairing(highest.getKey(),
     highest.getValue()));
     }

     return merges;
     }
     */
    /**
     * For the current cluster, finds the most similar cluster using the
     * provided linkage method and returns the pairing for it.
     *
     * @param assign
     * @param curCluster
     * @param linkage
     * @param similarityMatrix
     * @return
     */
    public static Pairing findMostSimilar(Map<Integer, Set<Integer>> assign, int curCluster,
            ClusterLinkage linkage, Matrix similarityMatrix) {
        // Start with with the most similar being set to the newly merged
        // cluster, as this value has already been computed
        double mostSimilar = -1;
        double similarity;
        Integer otherId;
        Integer paired = -1;
        for (Map.Entry<Integer, Set<Integer>> other : assign.entrySet()) {
            otherId = other.getKey();
            if (!otherId.equals(curCluster)) {
                //System.out.println("linkage: " + linkage);
                similarity = linkage.similarity(similarityMatrix, assign.get(curCluster), other.getValue());
                // System.out.println("similarity: (" + curCluster + " vs " + otherId + ")" + similarity);
                if (similarity > mostSimilar) {
                    mostSimilar = similarity;
                    paired = otherId;
                }
            }
        }
        //System.out.println("most similar: [" + mostSimilar + ", " + paired + "]");
        return new Pairing(mostSimilar, paired);
    }

    /**
     * Returns the final mapping of data points as an array where each row is
     * assigned to a single cluster value from 0 to <i>n</u>, the number of
     * clusters.
     *
     * @param assignment a mapping from cluster number to the data points (rows)
     *                   that are contained in it
     * @param p          the number of initial data points
     *
     * @return the cluster assignment
     */
    private static int[] toAssignArray(Map<Integer, Set<Integer>> assignment, int numDataPoints) {
        int[] clusters = new int[numDataPoints];
        for (int i = 0; i < numDataPoints; ++i) {
            clusters[i] = -1;
        }
        int clusterIndex = 0;
        for (Set<Integer> cluster : assignment.values()) {
            // Decide whether this cluster has already been assigned by picking
            // out the first element in the cluster and seeing if it has the
            // dummy cluster value (-1)
            System.out.println("cluster: " + cluster.toString());
            int r = cluster.iterator().next();
            if (clusters[r] != -1) {
                continue;
            }
            // Otherwise the row this cluster needs to be assigned a cluster
            // index
            for (int row : cluster) {
                clusters[row] = clusterIndex;
            }
            // Increment the cluster index for the next cluster
            clusterIndex++;
        }
        LOGGER.log(Level.INFO, "total number of clusters: {0}", clusterIndex);
        return clusters;
    }

    /**
     * Each data point forms an individual cluster
     *
     * @param numDataPoints the number of initial data points
     * @return
     */
    public static Map<Integer, Set<Integer>> generateInitialAssignment(int numDataPoints) {
        Map<Integer, Set<Integer>> clusterAssignment = new HashMap<Integer, Set<Integer>>(numDataPoints);
        for (int i = 0; i < numDataPoints; ++i) {
            HashSet<Integer> cluster = new HashSet<Integer>();
            cluster.add(i);
            clusterAssignment.put(i, cluster);
        }
        return clusterAssignment;
    }

    /**
     * Computes and returns the similarity matrix for {@code m} using the
     * specified similarity function
     *
     * @param m
     * @param dm
     * @return
     */
    public static Matrix computeRowSimilarityMatrix(Matrix m, DistanceMeasure dm) {
        Matrix similarityMatrix;

        if (dm.isSymmetric()) {
            similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    //double similarity =  Similarity.getSimilarity(similarityFunction, m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, dm.measure(m.getRowVector(i), m.getRowVector(j)));
                }
            }
        } else {
            double similarity;
            similarityMatrix = new JMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    similarity = dm.measure(m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, similarity);
                    similarityMatrix.set(j, i, similarity);
                }
            }
        }
        return similarityMatrix;
    }

    /**
     * Computes similarity matrix for columns
     *
     * @param m
     * @param dm
     * @return
     */
    public static Matrix computeColumnsSimilarityMatrix(Matrix m, DistanceMeasure dm) {
        Matrix similarityMatrix;

        if (dm.isSymmetric()) {
            similarityMatrix = new SymmetricMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    similarityMatrix.set(i, j, dm.measure(m.getColumnVector(i), m.getColumnVector(j)));
                }
            }
        } else {
            double similarity;
            similarityMatrix = new JMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    similarity = dm.measure(m.getColumnVector(i), m.getColumnVector(j));
                    similarityMatrix.set(i, j, similarity);
                    similarityMatrix.set(j, i, similarity);
                }
            }
        }
        return similarityMatrix;

    }
}
