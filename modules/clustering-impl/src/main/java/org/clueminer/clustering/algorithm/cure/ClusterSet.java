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
import java.util.Iterator;
import java.util.PriorityQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.knn.KDTree;
import org.clueminer.neighbor.Neighbor;
import org.openide.util.Exceptions;

/**
 * Creates a set of clusters for a given number of data points or reduces the
 * number of clusters to a fixed number of clusters as specified using CURE's
 * hierarchical clustering algorithm.
 *
 * The ClusterSet uses 2 data structures. The KD Tree is initialized and used to
 * store points across clusters. The Min Heap (Uses java.util.PriorityQueue) is
 * used to store the clusters and repetitively perform clustering. The Min Heap
 * is rearranged in every step to bring the closest pair of clusters to the root
 * of the heap and also change the closest distance measures for all clusters.
 *
 * Please refer to the CURE Hierarchical Clustering Algorithm for more details.
 * This class works only with the sampled partitioned data or already set of
 * clusters formed. The computation of set of clusters can be done remotely on a
 * machine hence adding concurrency to the overall algorithm.
 *
 * @author deric
 * @param <E> type of data to cluster
 * @param <C> clustering structure based on CURE cluster
 */
public class ClusterSet<E extends Instance, C extends CureCluster<E>> {

    private int numberOfPoints;
    private E[] points;
    CureComparator<E> cc;
    PriorityQueue<C> heap;
    private KDTree<E> kdtree;
    int clustersToBeFound;
    int numberofRepInCluster;
    double shrinkFactor;
    int newPointCount;
    private HashMap dataPointMap;
    private Distance dm;

    /**
     * Initialize the Containers
     *
     * @param numberOfPoints Number of Data Points
     * @param clustersToBeFound Clusters to be found after clustering
     * @param numberOfRepInCluster Number of Representative Points for every
     * Cluster
     * @param shrinkFactor Shrink Factor for a Cluster
     */
    private void initializeContainers(int numberOfPoints, int clustersToBeFound, int numberOfRepInCluster, double shrinkFactor) {
        this.numberOfPoints = numberOfPoints;
        points = (E[]) new Object[numberOfPoints];
        cc = new CureComparator<>();
        heap = new PriorityQueue(1000, cc);
        this.clustersToBeFound = clustersToBeFound;
        this.numberofRepInCluster = numberOfRepInCluster;
        this.shrinkFactor = shrinkFactor;
        newPointCount = numberOfPoints;
    }

    /**
     * Reduce the number of clusters to the specified numberOfClusters
     *
     * @param data Set of Clusters
     * @param numberOfClusters Number of Clusters to be found
     * @param numberOfRepInCluster Number of Representative Points in a Cluster
     * @param shrinkFactor Shrink Factor for Representative Points in a new
     * Cluster formed
     * @param dataPointMap Data Point Map
     * @param clusterMerge True
     */
    public ClusterSet(Clustering<E, C> data, int numberOfClusters, int numberOfRepInCluster, double shrinkFactor, HashMap dataPointMap, boolean clusterMerge) {
        numberOfPoints = data.instancesCount();
        dm = EuclideanDistance.getInstance();
        points = (E[]) new Object[numberOfPoints];
        cc = new CureComparator<>();
        heap = new PriorityQueue(1000, cc);
        kdtree = new KDTree();
        int pointIndex = 0;

        if (clusterMerge) {
            for (int i = 0; i < data.size(); i++) {
                C cluster = data.get(i);
                for (int j = 0; j < cluster.size(); j++) {
                    points[pointIndex] = cluster.get(j);
                    pointIndex++;
                }
            }
            clustersToBeFound = numberOfClusters;
            this.numberofRepInCluster = numberOfRepInCluster;
            this.shrinkFactor = shrinkFactor;
            this.dataPointMap = dataPointMap;
        }
        buildKDTree(data);
        buildHeapForClusters(data);
    }

    /**
     * Creates a set of clusters from the given number of data points and other
     * CURE parameters
     *
     * @param dataPoints Data Points to be clustered
     * @param numberOfClusters Number of Clusters to be formed
     * @param numberOfRepInCluster Number of Representative points in a new
     * cluster
     * @param shrinkFactor Shrink factor for Representative points
     * @param dataPointMap The HashMap to store the data points
     */
    public ClusterSet(ArrayList<E> dataPoints, int numberOfClusters, int numberOfRepInCluster, double shrinkFactor, HashMap dataPointMap) {
        dm = EuclideanDistance.getInstance();
        initializeContainers(dataPoints.size(), numberOfClusters, numberOfRepInCluster, shrinkFactor);
        initializePoints(dataPoints, dataPointMap);
        buildKDTree(dataPoints);
        buildHeap();
        startClustering();
    }

    /**
     * Build the heap for set of clusters specified
     *
     * @param clusters Set of Clusters
     */
    private void buildHeapForClusters(Clustering<E, C> clusters) {
        for (C clust : clusters) {
            heap.add(clust);
        }
    }

    /**
     * Initialize the data points
     *
     * @param dataPoints Data Points List
     * @param dataPointMap Map of Data Points
     */
    private void initializePoints(ArrayList<E> dataPoints, HashMap dataPointMap) {
        this.dataPointMap = dataPointMap;
        Iterator<E> iter = dataPoints.iterator();
        int index = 0;
        while (iter.hasNext()) {
            points[index] = iter.next();
            index++;
        }
    }

    /**
     * Merge the given set of clusters using CURE's hierarchical clustering
     * algorithm
     *
     * @return ArrayList Set of Merged Clusters
     */
    public ArrayList<C> mergeClusters() {
        ArrayList<C> mergedClusters = new ArrayList();
        startClustering();
        while (heap.size() != 0) {
            mergedClusters.add(heap.remove());
        }
        return mergedClusters;
    }

    /**
     * Gets all clusters present in the Min Heap
     *
     * @return Cluster[] Set of Clusters
     */
    public C[] getAllClusters() {
        C clusters[] = (C[]) new Cluster[heap.size()];
        int i = 0;
        while (heap.size() != 0) {
            clusters[i] = heap.remove();
            i++;
        }
        return clusters;
    }

    /**
     * Builds the KD Tree to store the data points
     */
    private void buildKDTree(ArrayList<E> dataPoints) {
        kdtree = new KDTree((Dataset) dataPoints);
    }

    private void buildKDTree(Clustering<E, C> dataPoints) {
        kdtree = new KDTree((Dataset) dataPoints);
    }

    /**
     * Builds the Initial Min Heap. Each point represents a cluster when the
     * algorithm begins. It creates each cluster and adds it to the heap.
     */
    private void buildHeap() {
        Clustering<E, C> clusters = new ClusterList<>();
        HashMap<Integer, C> pointCluster = new HashMap();
        for (int i = 0; i < numberOfPoints; i++) {
            C cluster = (C) new CureCluster();
            cluster.rep.add(points[i]);
            cluster.pointsInCluster.add(points[i]);

            Neighbor<E> nearest = kdtree.nearest(points[i]);
            cluster.distanceFromClosest = dm.measure(points[i], nearest.key);
            cluster.closestClusterRep.add(nearest.index);
            clusters.add(cluster);
            pointCluster.put(points[i].getIndex(), cluster);
        }
        for (int i = 0; i < clusters.size(); i++) {
            C cluster = clusters.get(i);
            int closest = cluster.closestClusterRep.get(0);
            //cluster.closestCluster = (Cluster)clusters.get(closest);
            cluster.closestCluster = pointCluster.get(closest);
            heap.add(cluster);
        }
    }

    /**
     * Initiates the clustering. The stopping condition is reached when the size
     * of heap equals number of clusters to be found. At every step two clusters
     * are merged and the heap is rearranged. The representative points are
     * deleted for old clusters and the representative points are added for new
     * cluster to the KD Tree.
     */
    private void startClustering() {
        while (heap.size() > clustersToBeFound) {
            C minCluster = heap.remove();
            C closestCluster = (C) minCluster.closestCluster;
            heap.remove(closestCluster);
            C newCluster = merge(minCluster, closestCluster);
            deleteAllRepPointsForCluster(minCluster);
            deleteAllRepPointsForCluster(closestCluster);
            insertAllRepPointsForCluster(newCluster);
            newCluster.closestCluster = minCluster;
            heap.add(newCluster);
            adjustHeap(newCluster, minCluster, closestCluster);
        }
    }

    /**
     * Adjust the heap after the new merged cluster has been added
     *
     * @param newCluster The merged cluster
     * @param oldcluster1 The Cluster 1 which was merged
     * @param oldCluster2 The Closest Cluster, Cluster2, to Cluster 1 which was
     * merged
     */
    public void adjustHeap(Cluster newCluster, Cluster oldcluster1, Cluster oldCluster2) {
        Clustering<E, C> clusters = new ClusterList<>();
        double distance;
        int initialHeapSize = heap.size();
        for (int i = 0; i < initialHeapSize; i++) {
            clusters.add(heap.remove());
        }
        for (int i = 0; i < clusters.size(); i++) {
            C cluster1 = clusters.get(i);
            if (!(cluster1.closestCluster == oldcluster1) && !(cluster1.closestCluster == oldCluster2)) {
                heap.add(cluster1);
                continue;
            }
            cluster1.distanceFromClosest = 100000;
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) {
                    continue;
                }
                C cluster2 = clusters.get(j);
                distance = cluster1.computeDistanceFromCluster(cluster2, dm);
                if (distance < cluster1.distanceFromClosest) {
                    cluster1.distanceFromClosest = distance;
                    cluster1.closestCluster = cluster2;
                }
            }
            heap.add(cluster1);
        }
    }

    /**
     * Insert all representative points of the cluster to the KD Tree
     *
     * @param cluster Merged Cluster
     */
    public void insertAllRepPointsForCluster(C cluster) {
        ArrayList<E> repPoints = cluster.rep;
        for (E point : repPoints) {
            kdtree.insert(point, point.getIndex());
        }
    }

    /**
     * Delete all representative points of the cluster from the KD Tree
     *
     * @param cluster Cluster which got merged
     */
    public void deleteAllRepPointsForCluster(C cluster) {
        ArrayList<E> repPoints = cluster.rep;
        for (E point : repPoints) {
            kdtree.delete(point);
        }
    }

    /**
     * Merge two clusters. Calculate the new representative points and shrink
     * them
     *
     * @param cluster1 Cluster 1 to be merged
     * @param cluster2 Cluster 2 to be merged
     * @return Cluster The Merged Cluster
     */
    public C merge(C cluster1, C cluster2) {
        CureCluster<E> newCluster = new CureCluster();
        for (E inst : cluster1) {
            newCluster.add(inst);
        }
        for (E inst : cluster2) {
            newCluster.add(inst);
        }
        E mean = newCluster.getCentroid();
        CureCluster<E> tempset = new CureCluster<>();
        for (int i = 0; i < numberofRepInCluster; i++) {
            double maxDist = 0;
            double minDist;
            E maxPoint = null;
            for (int j = 0; j < newCluster.size(); j++) {
                E p = newCluster.get(j);
                if (i == 0) {
                    minDist = dm.measure(p, mean);
                } else {
                    minDist = computeMinDistanceFromGroup(p, tempset);
                }
                if (minDist >= maxDist) {
                    maxDist = minDist;
                    maxPoint = p;
                }
            }
            tempset.add(maxPoint);
        }

        InstanceBuilder<E> builder = tempset.builder();
        for (int i = 0; i < tempset.size(); i++) {
            E p = tempset.get(i);
            E rep = builder.build();
            for (int j = 0; j < rep.size(); j++) {
                rep.set(j, p.get(j) * shrinkFactor * (mean.get(j) - p.get(j)));
            }
            //rep.index = newPointCount++;
            rep.setIndex(Cure.getCurrentRepCount());
            newCluster.rep.add(rep);
        }
        return (C) newCluster;
    }

    /**
     * Computes the min distance of a point from the group of points
     *
     * @param p Point p
     * @param group Group of points
     * @return double The Minimum Euclidean Distance
     */
    public double computeMinDistanceFromGroup(E p, CureCluster<E> group) {
        double minDistance = 100000;
        for (E q : group) {
            if (p.equals(q)) {
                continue;
            }
            double distance = dm.measure(p, q);
            if (minDistance > distance) {
                minDistance = distance;
            }
        }
        if (minDistance == 100000) {
            return 0;
        } else {
            return minDistance;
        }
    }

    /**
     * Show the clusters formed
     */
    public void showClusters() {
        for (int i = 0; i < clustersToBeFound; i++) {
            Cluster cluster = (Cluster) heap.remove();
            logCluster(cluster, "cluster" + i);
        }
    }

    /**
     * Logs the cluster to a file
     *
     * @param cluster Cluster
     * @param filename Name of the file
     */
    public void logCluster(Cluster<E> cluster, String filename) {
        FileWriter fw;
        try {
            fw = new FileWriter(filename, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.write("#\tX\tY\n");
            for (E inst : cluster) {
                for (int i = 0; i < inst.size(); i++) {
                    if (i > 0) {
                        out.write("\t");
                    }
                    out.write(String.valueOf(inst.value(i)));
                }
                out.write("\n");
            }
            out.flush();
            fw.close();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
}
