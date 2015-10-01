/*
 * Copyright (C) 2011-2015 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.algorithm.cure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * CURE - CLustering Using REpresentatives
 *
 * @cite Guha, Sudipto, Rajeev Rastogi, and Kyuseok Shim. "CURE: an efficient
 * clustering algorithm for large databases." ACM SIGMOD Record. Vol. 27. No. 2.
 * ACM, 1998.
 *
 * @author deric
 * @param <E>
 * @param <C> CURE requires cluster structure with set of representatives
 */
public class Cure<E extends Instance, C extends CureCluster<E>> extends AbstractClusteringAlgorithm<E, C> implements ClusteringAlgorithm<E, C> {

    /**
     * total number of points (instances) in the data set
     */
    private int n;
    /**
     * number of expected clusters
     */
    public final String K = "k";
    @Param(name = K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int numberOfClusters;

    public final String MIN_REPRESENTATIVES = "min_representatives";
    @Param(name = MIN_REPRESENTATIVES, description = "minimum number of representatives", required = false, min = 1, max = 1000)
    private int minRepresentativeCount;

    public final String SHRINK_FACTOR = "shrink_factor";
    @Param(name = SHRINK_FACTOR, description = "shrink factor", required = false, min = 0.0, max = 1.0)
    private double shrinkFactor;

    public final String REPRESENTATION_PROBABILITY = "representation_probablity";
    @Param(name = REPRESENTATION_PROBABILITY,
           description = "required representation probablity",
           min = 0.0, max = 1.0)
    private double representationProbablity;

    /**
     * For performance only. When clustering large datasets it is recommended to
     * split in so many parts, that each partition will fit into memory
     */
    public final String NUM_PARTITIONS = "num_partitions";
    @Param(name = NUM_PARTITIONS, description = "number of partitions", min = 1, max = 1000)
    private int numberOfPartitions;

    public final String REDUCE_FACTOR = "reduce_factor";
    @Param(name = REDUCE_FACTOR, description = "reduce factor for each partition", min = 1, max = 1000)
    private int reducingFactor;

    private ArrayList<E> outliers;
    private HashMap dataPointsMap;
    private static int currentRepAdditionCount;
    private HashSet<Integer> blacklist;

    public static final String name = "CURE";

    public Cure() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        n = dataset.size();
        initializeParameters(props);

        long beginTime = System.currentTimeMillis();

        int sampleSize = calculateSampleSize();
        ArrayList<E> randomPointSet = selectRandomPoints(dataset, sampleSize);

        ArrayList<E> partition;
        Clustering<E, C> clustering = (Clustering<E, C>) Clusterings.newList(numberOfClusters);
        Iterator<E> iter = randomPointSet.iterator();
        for (int i = 0; i < numberOfPartitions - 1; i++) {
            partition = new ArrayList<>(randomPointSet.size() / numberOfPartitions);
            int pointIndex = 0;
            while (pointIndex < dataset.size() / numberOfPartitions) {
                partition.add(iter.next());
                pointIndex++;
            }
            clusterPartition(partition, clustering);
        }
        partition = new ArrayList<>();
        while (iter.hasNext()) {
            partition.add(iter.next());
        }
        if (!partition.isEmpty()) {
            clusterPartition(partition, clustering);
        }

        labelRemainingDataPoints(dataset, clustering);

        long time = System.currentTimeMillis() - beginTime;

        System.out.println("The Algorithm took " + time + " milliseconds to complete.");

        showClusters(clustering);

        return clustering;
    }

    private void clusterPartition(ArrayList<E> partition, Clustering<E, C> clustering) {
        int numberOfClusterInEachPartition = n / (numberOfPartitions * reducingFactor);

        ClusterSet<E, C> clusterSet = new ClusterSet(partition, numberOfClusterInEachPartition, minRepresentativeCount, shrinkFactor, dataPointsMap);
        C[] clusters = clusterSet.getAllClusters();
        if (reducingFactor >= 10) {
            clustering.addAll(eliminateOutliers(clusters, 1));
        } else {
            clustering.addAll(eliminateOutliers(clusters, 0));
        }
    }

    /**
     * Initializes the Parameters
     *
     * @param args The Command Line Argument
     */
    private void initializeParameters(Props props) {
        numberOfClusters = props.getInt(K);
        minRepresentativeCount = props.getInt(MIN_REPRESENTATIVES, 5);
        shrinkFactor = props.getDouble(SHRINK_FACTOR, 0.5);
        representationProbablity = props.getDouble(REPRESENTATION_PROBABILITY, 0.1);
        numberOfPartitions = props.getInt(NUM_PARTITIONS, 1);
        reducingFactor = props.getInt(REDUCE_FACTOR, 2);

        dataPointsMap = new HashMap();
        currentRepAdditionCount = n;
        blacklist = new HashSet<>();
        outliers = new ArrayList();
    }

    /**
     * Calculates the Sample Size based on Chernoff Bounds Mentioned in the CURE
     * Algorithm
     *
     * @return int The Sample Data Size to be worked on
     */
    private int calculateSampleSize() {
        return (int) ((0.5 * n)
                + (numberOfClusters * Math.log10(1 / representationProbablity))
                + (numberOfClusters * Math.sqrt(Math.pow(Math.log10(1 / representationProbablity), 2)
                        + (n / numberOfClusters) * Math.log10(1 / representationProbablity))));
    }

    /**
     * Select random points from the data set
     *
     * @param sampleSize The sample size selected
     * @return ArrayList The Selected Random Points
     */
    private ArrayList selectRandomPoints(Dataset<E> dataset, int sampleSize) {
        ArrayList<E> randomPointSet = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < sampleSize; i++) {
            int index = random.nextInt(n);
            if (blacklist.contains(index)) {
                i--;
            } else {
                randomPointSet.add(dataset.get(index));
                blacklist.add(index);
            }
        }
        return randomPointSet;
    }

    /**
     * Eliminates outliers after pre-clustering
     *
     * @param clusters Clusters present
     * @param outlierEligibilityCount Min Threshold count for not being outlier
     * cluster
     */
    private ArrayList<C> eliminateOutliers(C[] clusters, int outlierEligibilityCount) {
        ArrayList<C> res = new ArrayList<>(clusters.length);
        for (C cluster : clusters) {
            if (cluster.size() > outlierEligibilityCount) {
                res.add(cluster);
            } else {
                outliers.addAll(cluster);
            }
        }
        return res;
    }

    /**
     * Assign all remaining data points which were not part of the sampled data
     * set to set of clusters formed
     *
     * @param clusters Set of clusters
     * @return ArrayList Modified clusters
     */
    private Clustering<E, C> labelRemainingDataPoints(Dataset<E> dataset, Clustering<E, C> clusters) {
        for (int index = 0; index < dataset.size(); index++) {
            if (blacklist.contains(index)) {
                continue;
            }
            E inst = dataset.get(index);
            double smallestDistance = 1000000;
            int nearestClusterIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                ArrayList<E> rep = clusters.get(i).rep;
                for (E other : rep) {
                    double distance = distanceFunction.measure(inst, other);
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        nearestClusterIndex = i;
                    }
                }
            }
            if (nearestClusterIndex != -1) {
                clusters.get(nearestClusterIndex).add(inst);
            }
        }
        return clusters;
    }

    private void debug(Exception e) {
        //e.printStackTrace(System.out);
    }

    /**
     * Gets the current representative count so that the new points added do not
     * conflict with older KD Tree indices
     *
     * @return int Next representative count
     */
    public static int getCurrentRepCount() {
        return ++currentRepAdditionCount;
    }

    public void showClusters(Clustering<E, C> clusters) {
        for (int i = 0; i < numberOfClusters; i++) {
            Cluster<E> cluster = clusters.get(i);
            logCluster(cluster, "cluster" + i);
        }
        logOutlier();
        logPlotScript(clusters.size());
    }

    private BufferedWriter getWriterHandle(String filename) {
        BufferedWriter out = null;
        try {
            FileWriter fw = new FileWriter(filename, true);
            out = new BufferedWriter(fw);
        } catch (Exception e) {
            debug(e);
        }
        return out;
    }

    private void closeWriterHandle(BufferedWriter out) {
        try {
            out.flush();
            out.close();
        } catch (Exception e) {
            debug(e);
        }
    }

    private void logCluster(Cluster<E> cluster, String filename) {
        BufferedWriter out = getWriterHandle(filename);
        try {
            out.write("#\tX\tY\n");
            for (int j = 0; j < cluster.size(); j++) {
                E p = cluster.get(j);
                for (int i = 0; i < p.size(); i++) {
                    if (i > 0) {
                        out.write("\t");
                    }
                    out.write(String.valueOf(p.value(i)));
                }
                out.write("\n");
            }
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void logOutlier() {
        BufferedWriter out = getWriterHandle("outliers");
        try {
            out.write("#\tX\tY\n");
            for (E outlier : outliers) {
                for (int i = 0; i < outlier.size(); i++) {
                    if (i > 0) {
                        out.write("\t");
                    }
                    out.write(String.valueOf(outlier.value(i)));
                }
                out.write("\n");
            }
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void logPlotScript(int totalClusters) {
        BufferedWriter out = getWriterHandle("plotcure.txt");
        try {
            setPlotStyle(out);
            out.write("plot");
            for (int i = 0; i < totalClusters; i++) {
                out.write(" \"cluster" + i + "\",");
            }
            out.write(" \"outliers\"");
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void setPlotStyle(BufferedWriter out) {
        try {
            out.write("reset\n");
            out.write("set size ratio 2\n");
            out.write("unset key\n");
            out.write("set title \"CURE\"\n");
        } catch (Exception e) {
            debug(e);
        }
    }

}
