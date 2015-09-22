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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.knn.KDTree;

/**
 *
 * Creates a set of clusters for a given number of data points or reduces the
 * number of clusters to a fixed number of clusters as specified
 * using CURE's hierarchical clustering algorithm.
 *
 * The ClusterSet uses 2 data structures. The KD Tree is initialized and used to
 * store points across clusters.
 * The Min Heap (Uses java.util.PriorityQueue) is used to store the clusters and
 * repetitively perform clustering. The Min Heap is rearranged
 * in every step to bring the closest pair of clusters to the root of the heap
 * and also change the closest distance measures for all clusters.
 *
 * Please refer to the CURE Hierarchical Clustering Algorithm for more details.
 * This class works only with the sampled partitioned data
 * or already set of clusters formed. The computation of set of clusters can be
 * done remotely on a machine hence adding concurrency to the
 * overall algorithm.<
 *
 * @author deric
 */
public class ClusterSet {

    int numberOfPoints;
    Point[] points;
    CompareCluster cc;
    PriorityQueue heap;
    KDTree kdtree;
    int clustersToBeFound;
    int numberofRepInCluster;
    double shrinkFactor;
    int newPointCount;
    HashMap dataPointMap;

    /**
     * Initialize the Containers
     *
     * @param numberOfPoints Number of Data Points
     * @param clustersToBeFound Clusters to be found after clustering
     * @param numberOfRepInCluster Number of Representative Points for every
     * Cluster
     * @param shrinkFactor Shrink Factor for a Cluster
     */
    public void initializeContainers(int numberOfPoints, int clustersToBeFound, int numberOfRepInCluster, double shrinkFactor) {
        this.numberOfPoints = numberOfPoints;
        points = new Point[numberOfPoints];
        cc = new CompareCluster();
        heap = new PriorityQueue(1000, cc);
        kdtree = new KDTree(2);
        this.clustersToBeFound = clustersToBeFound;
        this.numberofRepInCluster = numberOfRepInCluster;
        this.shrinkFactor = shrinkFactor;
        newPointCount = numberOfPoints;
    }

    /**
     * Reduce the number of clusters to the specified numberOfClusters
     *
     * @param clusters Set of Clusters
     * @param numberOfClusters Number of Clusters to be found
     * @param numberOfRepInCluster Number of Representative Points in a Cluster
     * @param shrinkFactor Shrink Factor for Representative Points in a new
     * Cluster formed
     * @param dataPointMap Data Point Map
     * @param clusterMerge True
     */
    public ClusterSet(ArrayList clusters, int numberOfClusters, int numberOfRepInCluster, double shrinkFactor, HashMap dataPointMap, boolean clusterMerge) {
        numberOfPoints = 0;
        for (int i = 0; i < clusters.size(); i++) {
            numberOfPoints += ((Cluster) clusters.get(i)).getClusterSize();
        }
        points = new Point[numberOfPoints];
        cc = new CompareCluster();
        heap = new PriorityQueue(1000, cc);
        kdtree = new KDTree(2);
        int pointIndex = 0;

        if (clusterMerge) {
            for (int i = 0; i < clusters.size(); i++) {
                Cluster cluster = (Cluster) clusters.get(i);
                for (int j = 0; j < cluster.getClusterSize(); j++) {
                    points[pointIndex] = (Point) cluster.pointsInCluster.get(j);
                    pointIndex++;
                }
            }
            clustersToBeFound = numberOfClusters;
            this.numberofRepInCluster = numberOfRepInCluster;
            this.shrinkFactor = shrinkFactor;
            this.dataPointMap = dataPointMap;
        }
        buildKDTree();
        buildHeapForClusters(clusters);
    }

    /**
     * Build the heap for set of clusters specified
     *
     * @param clusters Set of Clusters
     */
    public void buildHeapForClusters(ArrayList clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            heap.add((Cluster) clusters.get(i));
        }
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
    public ClusterSet(ArrayList dataPoints, int numberOfClusters, int numberOfRepInCluster, double shrinkFactor, HashMap dataPointMap) {
        initializeContainers(dataPoints.size(), numberOfClusters, numberOfRepInCluster, shrinkFactor);
        initializePoints(dataPoints, dataPointMap);
        buildKDTree();
        buildHeap();
        startClustering();
    }

    /**
     * Initialize the data points
     *
     * @param dataPoints Data Points List
     * @param dataPointMap Map of Data Points
     */
    public void initializePoints(ArrayList dataPoints, HashMap dataPointMap) {
        this.dataPointMap = dataPointMap;
        Iterator iter = dataPoints.iterator();
        int index = 0;
        while (iter.hasNext()) {
            Point point = (Point) iter.next();
            points[index] = point;
            index++;
        }
    }

    /**
     * Merge the given set of clusters using CURE's hierarchical clustering
     * algorithm
     *
     * @return
     * ArrayList Set of Merged Clusters
     */
    public ArrayList mergeClusters() {
        ArrayList mergedClusters = new ArrayList();
        startClustering();
        while (heap.size() != 0) {
            mergedClusters.add(heap.remove());
        }
        return mergedClusters;
    }

    /**
     * Gets all clusters present in the Min Heap
     *
     * @return
     * Cluster[] Set of Clusters
     */
    public Cluster[] getAllClusters() {
        Cluster clusters[] = new Cluster[heap.size()];
        int i = 0;
        while (heap.size() != 0) {
            clusters[i] = (Cluster) heap.remove();
            i++;
        }
        return clusters;
    }

    /**
     * Builds the KD Tree to store the data points
     */
    public void buildKDTree() {
        for (Integer i = 0; i < numberOfPoints; i++) {
            try {
                kdtree.insert(points[i].toDouble(), points[i].index);
            } catch (Exception e) {
                debug(e);
            }
        }
    }

    /**
     * Builds the Initial Min Heap. Each point represents a cluster when the
     * algorithm begins. It creates each cluster and adds it to the heap.
     */
    public void buildHeap() {
        ArrayList clusters = new ArrayList();
        HashMap pointCluster = new HashMap();
        for (int i = 0; i < numberOfPoints; i++) {
            Cluster cluster = new Cluster();
            cluster.rep.add(points[i]);
            cluster.pointsInCluster.add(points[i]);
            int nearestPoint = getNearestNeighbour(points[i]);
            Point nearest = (Point) dataPointMap.get(nearestPoint);
            cluster.distanceFromClosest = points[i].calcDistanceFromPoint(nearest);	//changed here from indexing to hashmap
            cluster.closestClusterRep.add(nearestPoint);
            clusters.add(cluster);
            pointCluster.put(points[i].index, cluster);
        }
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = (Cluster) clusters.get(i);
            int closest = (Integer) cluster.closestClusterRep.get(0);
            //cluster.closestCluster = (Cluster)clusters.get(closest);
            cluster.closestCluster = (Cluster) pointCluster.get((Integer) closest);
            heap.add(cluster);
        }
    }

    /**
     * Get the nearest neighbor for a given point
     *
     * @param point Point point
     * @return
     * int KD Tree index of the nearest neighbor
     */
    public int getNearestNeighbour(Point point) {
        int result = 0;
        try {
            Object[] nearestPoint = kdtree.nearest(point.toDouble(), 2);
            result = (Integer) nearestPoint[1];
        } catch (Exception e) {
            debug(e);
            result = -1;
        }
        return result;
    }

    /**
     * Initiates the clustering. The stopping condition is reached when the size
     * of heap equals number of clusters to be found.
     * At every step two clusters are merged and the heap is rearranged. The
     * representative points are deleted for old clusters and
     * the representative points are added for new cluster to the KD Tree.
     */
    public void startClustering() {
        while (heap.size() > clustersToBeFound) {
            Cluster minCluster = (Cluster) heap.remove();
            Cluster closestCluster = minCluster.closestCluster;
            heap.remove(closestCluster);
            Cluster newCluster = merge(minCluster, closestCluster);
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
        ArrayList clusters = new ArrayList();
        int initialHeapSize = heap.size();
        for (int i = 0; i < initialHeapSize; i++) {
            clusters.add(heap.remove());
        }
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster1 = (Cluster) clusters.get(i);
            if (!(cluster1.closestCluster == oldcluster1) && !(cluster1.closestCluster == oldCluster2)) {
                heap.add(cluster1);
                continue;
            }
            cluster1.distanceFromClosest = 100000;
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) {
                    continue;
                }
                Cluster cluster2 = (Cluster) clusters.get(j);
                double distance = cluster1.computeDistanceFromCluster(cluster2);
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
    public void insertAllRepPointsForCluster(Cluster cluster) {
        ArrayList repPoints = cluster.rep;
        for (int i = 0; i < repPoints.size(); i++) {
            Point point = (Point) repPoints.get(i);
            try {
                kdtree.insert(point.toDouble(), point.index);
            } catch (Exception e) {
                //debug(e);
            }
        }
    }

    /**
     * Delete all representative points of the cluster from the KD Tree
     *
     * @param cluster Cluster which got merged
     */
    public void deleteAllRepPointsForCluster(Cluster cluster) {
        ArrayList repPoints = cluster.rep;
        for (int i = 0; i < repPoints.size(); i++) {
            Point point = (Point) repPoints.get(i);
            try {
                kdtree.delete(point.toDouble());
            } catch (Exception e) {
                //debug(e);
            }
        }
    }

    /**
     * Computes the mean point of the cluster
     *
     * @param cluster Cluster
     * @return
     * Point The Mean Point of the Cluster
     */
    public Point computeMeanOfCluster(Cluster cluster) {
        Point point = new Point();
        for (int i = 0; i < cluster.pointsInCluster.size(); i++) {
            point.x += ((Point) cluster.pointsInCluster.get(i)).x;
            point.y += ((Point) cluster.pointsInCluster.get(i)).y;
        }
        point.x /= cluster.pointsInCluster.size();
        point.y /= cluster.pointsInCluster.size();
        return point;
    }

    /**
     * Merge two clusters. Calculate the new representative points and shrink
     * them
     *
     * @param cluster1 Cluster 1 to be merged
     * @param cluster2 Cluster 2 to be merged
     * @return
     * Cluster The Merged Cluster
     */
    public Cluster merge(Cluster cluster1, Cluster cluster2) {
        Cluster newCluster = new Cluster();
        for (int i = 0; i < cluster1.pointsInCluster.size(); i++) {
            newCluster.pointsInCluster.add(cluster1.pointsInCluster.get(i));
        }
        for (int i = 0; i < cluster2.pointsInCluster.size(); i++) {
            newCluster.pointsInCluster.add(cluster2.pointsInCluster.get(i));
        }
        Point mean = computeMeanOfCluster(newCluster);
        ArrayList tempset = new ArrayList();
        for (int i = 0; i < numberofRepInCluster; i++) {
            double maxDist = 0;
            double minDist = 0;
            Point maxPoint = null;
            for (int j = 0; j < newCluster.pointsInCluster.size(); j++) {
                Point p = (Point) newCluster.pointsInCluster.get(j);
                if (i == 0) {
                    minDist = p.calcDistanceFromPoint(mean);
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

        for (int i = 0; i < tempset.size(); i++) {
            Point p = (Point) tempset.get(i);
            Point rep = new Point();
            rep.x = p.x + shrinkFactor * (mean.x - p.x);
            rep.y = p.y + shrinkFactor * (mean.y - p.y);
            //rep.index = newPointCount++;
            rep.index = Cure.getCurrentRepCount();
            newCluster.rep.add(rep);
        }
        return newCluster;
    }

    /**
     * Computes the min distance of a point from the group of points
     *
     * @param p Point p
     * @param group Group of points
     * @return
     * double The Minimum Euclidean Distance
     */
    public double computeMinDistanceFromGroup(Point p, ArrayList group) {
        double minDistance = 100000;
        for (int i = 0; i < group.size(); i++) {
            Point q = (Point) group.get(i);
            if (p.equals(q)) {
                continue;
            }
            double distance = p.calcDistanceFromPoint(q);
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
     * Print the Exception thrown
     *
     * @param e Exception e
     */
    public void debug(Exception e) {
        //e.printStackTrace(System.out);
    }

    /**
     * Logs the cluster to a file
     *
     * @param cluster Cluster
     * @param filename Name of the file
     */
    public void logCluster(Cluster cluster, String filename) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.write("#\tX\tY\n");
            for (int j = 0; j < cluster.pointsInCluster.size(); j++) {
                Point p = (Point) cluster.pointsInCluster.get(j);
                out.write("\t" + p.x + "\t" + p.y + "\n");
            }
            out.flush();
            fw.close();
        } catch (Exception e) {
            debug(e);
        }
    }
}
