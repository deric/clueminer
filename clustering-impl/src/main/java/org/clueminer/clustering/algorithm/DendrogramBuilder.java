package org.clueminer.clustering.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.Pairing;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.exec.WorkQueue;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.OnDiskMatrix;

/**
 *
 * @author Tomas Barton
 */
public class DendrogramBuilder {

    private final WorkQueue workQueue;
    private static final Logger LOGGER = Logger.getLogger(DendrogramBuilder.class.getName());

    /**
     * Builds a dendrogram of the rows of similarity matrix by iteratively
     * linking each row according to the linkage policy in a bottom up manner.
     * The dendrogram is represented as a series of merge steps for the rows of
     * the similarity matrix, where each row is initially assigned to its own
     * cluster. By following a sequence of merge operations, a particular
     * partitioning of the rows of {@code m} can be determined. For example, to
     * find the partitioning after 4 merge operations, one might do the
     * following:
     *
     * <pre>
     *   Matrix matrix;
     *   List<Merge> merges = buildDendogram(matrix, ...);
     *   List<Merge> fourMergeSteps = merges.subList(0, 4);
     *   MultiMap<Integer,Integer> clusterToRows =  new HashMultiMap<Integer,Integer>();
     *   for (int i = 0; i < matrix.rows(); ++i)
     *       clusterToElements.put(i, i);
     *
     *   for (Merge m : fourMergeSteps) {
     *       clusterToElements.putMulti(m.remainingCluster(),
     *           clusterToElements.remove(m.mergedCluster()));
     *   }
     *</pre>
     *
     * The resulting {@link edu.ucla.sspace.util.MultiMap} {@code clusterToRows}
     * contains the mapping from each cluster to the rows that are a part of it.
     *
     * @param m a matrix whose rows are to be compared and agglomeratively
     * merged into clusters
     * @param linkage how two clusters should be compared for similarity when
     * deciding which clusters to merge together
     * @param similarityFunction how to compare two rows of a matrix for
     * similarity
     *
     * @return a dendrogram corresponding to the merge steps for each cluster,
     * where each row is initially assigned to its own cluster whose id is the
     * same as its row's index
     */
    public DendrogramBuilder() {
        this.workQueue = WorkQueue.getWorkQueue();
    }

    public List<Merge> buildDendogram(Matrix m, ClusterLinkage linkage, DistanceMeasure similarityFunction) {

        int rows = m.rowsCount();
        LOGGER.log(Level.FINER, "Generating similarity matrix for {0} data points", rows);
        Matrix similarityMatrix = HierarchicalAgglomerativeClustering.computeRowSimilarityMatrix(m, similarityFunction);
        return buildDendrogram(similarityMatrix, linkage);
    }

    /**
     * Builds a dendrogram of the rows of similarity matrix by iteratively
     * linking each row according to the linkage policy in a bottom up manner.
     * The dendrogram is represented as a series of merge steps for the rows of
     * the similarity matrix, where each row is initially assigned to its own
     * cluster.
     *
     * @param similarityMatrix a square matrix whose (i, j) values denote the
     * similarity of row i to row j.
     *
     * @return a dendrogram corresponding to the merge steps for each cluster,
     * where each row is initially assigned to its own cluster whose id is the
     * same as its row's index
     *
     * @throws IllegalArgumentException if {@code similarityMatrix} is not a
     * square matrix
     */
    public List<Merge> buildDendrogram(Matrix similarityMatrix, ClusterLinkage linkage) {

        if (similarityMatrix.rowsCount() != similarityMatrix.columnsCount()) {
            throw new IllegalArgumentException("Similarity matrix must be square, got rows = " + similarityMatrix.rowsCount() + ", cols = " + similarityMatrix.columnsCount());
        }

        if (!(similarityMatrix instanceof OnDiskMatrix)) {
            LOGGER.fine("Similarity matrix supports fast multi-threaded access; switching to multi-threaded clustering");
            return buildDendogramMultithreaded(similarityMatrix, linkage);
        }

        int rows = similarityMatrix.rowsCount();

        // Create the initial set of clusters where each row is originally in
        // its own cluster
        final Map<Integer, Set<Integer>> clusterAssignment = HierarchicalAgglomerativeClustering.generateInitialAssignment(rows);
        System.out.println("assignments: "+clusterAssignment.toString());
        LOGGER.log(Level.INFO, "Calculating initial inter-cluster similarity using {0}", linkage);
        // Generate the initial set of cluster pairings based on the highest
        // similarity.  This mapping will be update as the number of clusters
        // are reduced, where merging a cluster will causes all the pairings
        // pointing to it constinuents recalculated.
        final Map<Integer, Pairing> clusterSimilarities = new HashMap<Integer, Pairing>();

        // For each cluster, find the most similar cluster
        for (Integer clusterId : clusterAssignment.keySet()) {
            clusterSimilarities.put(clusterId, HierarchicalAgglomerativeClustering.findMostSimilar(clusterAssignment, clusterId,
                    linkage, similarityMatrix));
        }

        LOGGER.finer("Assigning clusters");
        List<Merge> merges = new ArrayList<Merge>(rows - 1);

        // Perform rows-1 merges to merge all elements
        for (int mergeIter = 0; mergeIter < rows - 1; ++mergeIter) {
            //LOGGER.log(Level.FINER, "Computing dendogram merge{0}/{1}", new Object[]{mergeIter, rows - 1});

            // Find the two clusters that have the highest similarity
            int cluster1index = 0;
            int cluster2index = 0;
            double highestSimilarity = -1;

            // For each cluster, look at the cluster with the highest
            // similarity, and select the two with the global max
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

            // Order the indices so that the smaller index is first
            if (cluster1index > cluster2index) {
                int tmp = cluster2index;
                cluster2index = cluster1index;
                cluster1index = tmp;
            }

            // Track that the two clusters will be merged.  Always use the lower
            // of the two values as the new cluster assignment.
            Merge merge = new Merge(cluster1index, cluster2index, highestSimilarity);
            merges.add(merge);

            Set<Integer> cluster1 = clusterAssignment.get(cluster1index);
            Set<Integer> cluster2 = clusterAssignment.get(cluster2index);

            LOGGER.log(Level.FINER, "Merged cluster {0} with {1}, similarity {2}",
                    new Object[]{cluster1index, cluster2index, highestSimilarity});

            // Update the cluster assignments, adding in elements from the
            // second cluster and remove all references to the second merged-in
            // cluster.
            cluster1.addAll(cluster2);
            clusterAssignment.remove(cluster2index);
            clusterSimilarities.remove(cluster2index);

            // When down to just one cluster, stop the iteration
            if (clusterAssignment.size() == 1) {
                break;
            }

            // Local state variables to use while recalculating the similarities
            double mostSimilarToMerged = -1;
            Integer mostSimilarToMergedId = null;

            // Recalculate the inter-cluster similarity of a cluster in two
            // cases:
            // 
            // 1) a cluster that paired with either of these two (i.e. was most
            // similar to one of them before the merge).  
            //
            // 2) the most similar cluster to the newly merged cluster
            for (Map.Entry<Integer, Pairing> e : clusterSimilarities.entrySet()) {
                Integer clusterId = e.getKey();
                // Skip self comparisons for the merged clustering
                if (clusterId == cluster1index) {
                    continue;
                }
                // First, calculate the similarity between this cluster and the
                // newly merged cluster
                double simToNewCluster = linkage.similarity(similarityMatrix, cluster1, clusterAssignment.get(clusterId));

                // If this cluster is now the most similar to the newly-merged
                // cluster update its mapping
                if (simToNewCluster > mostSimilarToMerged) {
                    mostSimilarToMerged = simToNewCluster;
                    mostSimilarToMergedId = clusterId;
                }

                // Second, if the pair was previously paired with one of the
                // merged clusters, recompute what its most similar is
                Pairing p = e.getValue();
                if (p.pairedIndex == cluster1index
                        || p.pairedIndex == cluster2index) {
                    // Reassign with the new most-similar
                    e.setValue(HierarchicalAgglomerativeClustering.findMostSimilar(clusterAssignment, clusterId,
                            linkage, similarityMatrix));
                }
            }

            // Update the new most similar to the newly-merged cluster
            clusterSimilarities.put(cluster1index, new Pairing(mostSimilarToMerged, mostSimilarToMergedId));
        }

        return merges;

    }

    private List<Merge> buildDendogramMultithreaded(final Matrix similarityMatrix, final ClusterLinkage linkage) {

        int rows = similarityMatrix.rowsCount();

        /**
         * Create the initial set of clusters where each row is originally in
         * its own cluster
         */
        final Map<Integer, Set<Integer>> clusterAssignment = HierarchicalAgglomerativeClustering.generateInitialAssignment(rows);

        LOGGER.log(Level.FINER, "Calculating initial inter-cluster similarity using {0}", linkage);
        // Generate the initial set of cluster pairings based on the highest
        // similarity.  This mapping will be update as the number of clusters
        // are reduced, where merging a cluster will causes all the pairings
        // pointing to it constinuents recalculated.
        final Map<Integer, Pairing> clusterSimilarities =
                new ConcurrentHashMap<Integer, Pairing>(clusterAssignment.size());

        // For each cluster, find the most similar cluster.  Use the current
        // thread as the task key so any other thread executing this method
        // won't conflict.
        Object taskKey = workQueue.registerTaskGroup(clusterAssignment.size());
        for (Integer clusterId : clusterAssignment.keySet()) {
            final Integer clustId = clusterId;
            workQueue.add(taskKey, new Runnable() {
                @Override
                public void run() {
                    clusterSimilarities.put(clustId,
                            HierarchicalAgglomerativeClustering.findMostSimilar(clusterAssignment, clustId,
                            linkage, similarityMatrix));
                }
            });
        }
        workQueue.await(taskKey);

        LOGGER.finer("Assigning clusters");
        List<Merge> merges = new ArrayList<Merge>(rows - 1);


        // Perform rows-1 merges to merge all elements
        for (int mergeIter = 0; mergeIter < rows - 1; ++mergeIter) {
            LOGGER.log(Level.FINER, "Computing dendogram merge {0}", mergeIter);
            System.out.println("Computing dendogram merge "
                    + mergeIter + "/" + (rows - 1));


            // Find the two clusters that have the highest similarity
            int cluster1index = 0;
            int cluster2index = 0;
            double highestSimilarity = -1;

            // For each cluster, look at the cluster with the highest
            // similarity, and select the two with the global max
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

            // Order the indices so that the smaller index is first
            if (cluster1index > cluster2index) {
                int tmp = cluster2index;
                cluster2index = cluster1index;
                cluster1index = tmp;
            }

            // Track that the two clusters will be merged.  Always use the lower
            // of the two values as the new cluster assignment.
            Merge merge = new Merge(cluster1index, cluster2index, highestSimilarity);
            merges.add(merge);

            final Set<Integer> cluster1 = clusterAssignment.get(cluster1index);
            Set<Integer> cluster2 = clusterAssignment.get(cluster2index);

            LOGGER.log(Level.FINER,
                    "Merged cluster {0} with {1}, similarity {2}",
                    new Object[]{cluster1index, cluster2index,
                highestSimilarity});

            // Update the cluster assignments, adding in elements from the
            // second cluster and remove all references to the second merged-in
            // cluster.
            cluster1.addAll(cluster2);
            clusterAssignment.remove(cluster2index);
            clusterSimilarities.remove(cluster2index);

            // When down to just one cluster, stop the iteration
            if (clusterAssignment.size() == 1) {
                break;
            }

            // Recalculate the inter-cluster similarity of a cluster in two
            // cases:
            // 
            // 1) a cluster that paired with either of these two (i.e. was most
            // similar to one of them before the merge).  
            //
            // 2) the most similar cluster to the newly merged cluster
            final ConcurrentNavigableMap<Double, Integer> mostSimilarMap = new ConcurrentSkipListMap<Double, Integer>();
            // Use size()-1 as the number of tasks because we skip adding a task
            // for computing the new cluster's similarity to itself
            taskKey = workQueue.registerTaskGroup(clusterSimilarities.size() - 1);

            for (Map.Entry<Integer, Pairing> entry : clusterSimilarities.entrySet()) {

                // Thread-local state variables
                final Map.Entry<Integer, Pairing> e = entry;
                final Integer clusterId = e.getKey();
                final Pairing p = e.getValue();
                final int c1index = cluster1index;
                final int c2index = cluster2index;

                // Skip self comparisons for the merged clustering
                if (clusterId == c1index) {
                    continue;
                }

                workQueue.add(taskKey, new Runnable() {
                    public void run() {
                        // Task-local state variables to use while
                        // recalculating the similarities
                        double mostSimilarToMerged = -1;
                        Integer mostSimilarToMergedId = null;

                        // First, calculate the similarity between this
                        // cluster and the newly merged cluster
                        double simToNewCluster =
                                linkage.similarity(similarityMatrix, cluster1,
                                clusterAssignment.get(clusterId));

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
                            e.setValue(HierarchicalAgglomerativeClustering.findMostSimilar(clusterAssignment,
                                    clusterId, linkage, similarityMatrix));
                        }

                        // Once all of the cluster for this thread has been
                        // processed, update the similarity map.
                        mostSimilarMap.put(mostSimilarToMerged,
                                mostSimilarToMergedId);
                    }
                });
            }

            // Run each thread's comparisons
            workQueue.await(taskKey);

            // Collect the results from the similarity map.  The highest
            // similarity should be the largest key in the map, with the
            // clustering as the value.  Note that if there were ties in the
            // highest similarity, the cluster is selected by the last thread,
            // which is still arbitrarily fair.
            Map.Entry<Double, Integer> highest = mostSimilarMap.lastEntry();

            // Update the new most similar to the newly-merged cluster
            clusterSimilarities.put(cluster1index,
                    new Pairing(highest.getKey(),
                    highest.getValue()));
        }

        return merges;
    }
}
