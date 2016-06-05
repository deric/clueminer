/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.distance.api.Distance;
import org.clueminer.kdtree.KDTree;
import org.clueminer.kdtree.KeyDuplicateException;
import org.clueminer.kdtree.KeyMissingException;
import org.clueminer.kdtree.KeySizeException;
import org.clueminer.utils.Props;
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

    CureComparator<E> cc;
    PriorityQueue<C> heap;
    private KDTree<C> kdtree;
    //number of clusters to be found
    int k;
    int numberofRepInCluster;
    double shrinkFactor;
    int newPointCount;
    private Distance dm;
    private Clustering<E, C> clustering;
    private static int clusterCnt;

    public ClusterSet(Dataset<E> dataset, int numberOfClusters, Props props, Distance dist) {
        numberofRepInCluster = props.getInt(CURE.NUM_REPRESENTATIVES, 10);
        shrinkFactor = props.getDouble(CURE.SHRINK_FACTOR, 0.3);
        clusterCnt = 0;
        dm = dist;
        cc = new CureComparator<>();
        k = numberOfClusters;

        try {
            buildHeapAndTree(dataset);
            startClustering();
        } catch (KeySizeException ex) {
            //throw up :)
            throw new RuntimeException(ex);
        }
    }

    private C createCluster(Dataset<E> dataset) {
        CureCluster<E> cluster = new CureCluster<>();
        cluster.setClusterId(clusterCnt++);
        cluster.setAttributes(dataset.getAttributes());
        return (C) cluster;
    }

    /**
     * Check whether we have clusters on the heap
     *
     * @return
     */
    public boolean hasClusters() {
        return heap.size() > 0;
    }

    /**
     * Remove cluster from the heap
     *
     * @return
     */
    public C remove() {
        return heap.remove();
    }

    public int size() {
        return heap.size();
    }

    /**
     * Initialize heap and the k-d tree
     *
     * @param dataset
     * @param skipped list of item with same coordinates
     * @throws KeySizeException
     */
    private void buildHeapAndTree(Dataset<E> dataset) throws KeySizeException {
        kdtree = new KDTree<>(dataset.attributeCount());
        heap = new PriorityQueue(dataset.size(), cc);
        clustering = new ClusterList<>();
        C cluster;
        C nn;
        double[] key;
        for (E instance : dataset) {
            cluster = createCluster(dataset);
            cluster.rep.add(instance);
            cluster.add(instance);
            key = instance.arrayCopy();
            try {
                kdtree.insert(key, cluster);
                clustering.add(cluster);
            } catch (KeyDuplicateException ex) {
                //exactly same instances, put them both to the same cluster
                cluster = kdtree.search(key);
                cluster.add(instance);
            }
        }
        //when all instances are assigned to a cluster, update closest cluster
        for (int i = 0; i < clustering.size(); i++) {
            cluster = clustering.get(i);

            nn = nearest(cluster.get(0), 1);
            cluster.distClosest = dm.measure(cluster.get(0), nn.rep.get(0));
            //cluster.closestClusterRep = nn;
            cluster.closest = nn;
            heap.add(cluster);
        }
    }

    /**
     * Find k-th nearest neighbors
     *
     * @param needle
     * @param k with 1 retrieves the nearest neighbor
     * @return
     */
    private C nearest(E needle, int k) {
        List<C> nn;
        C nearest = null;
        int upBound = 1;
        try {
            nn = kdtree.nearest(needle.arrayCopy(), k + upBound);
            if (nn.isEmpty()) {
                return null;
            }
            //exclude needle from the nearest neighbors
            //nearest could be other representatives from the very same cluster
            nearest = nn.get(0);

        } catch (KeySizeException | IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
        return nearest;
    }

    private C nearest(E needle, double maxDist, int excludeClusterId) {
        List<C> nn;
        try {
            nn = kdtree.nearestEuclidean(needle.arrayCopy(), maxDist);
            if (nn.isEmpty()) {
                return null;
            }
            //TODO: maybe we should iterate in reverse order
            for (C near : nn) {
                if (near.getClusterId() != excludeClusterId) {
                    return near;
                }
            }

        } catch (KeySizeException | IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Main clustering procedure. The stopping condition is reached when the
     * size of heap equals number of clusters to be found. At every step two
     * clusters are merged and the heap is rearranged. The representative points
     * are deleted for old clusters and the representative points are added for
     * new cluster into the KD-Tree.
     */
    private void startClustering() {
        C u, v, w;
        while (heap.size() > k) {
            CURE.LOGGER.log(Level.FINEST, "heap size = {0}", heap.size());
            try {
                //extract_min(Q)
                u = heap.remove();
                v = (C) u.closest;
                if (u.equals(v)) {
                    System.out.println("merging same clusters!!! " + u.getClusterId());
                }
                heap.remove(v);
                w = merge(u, v);
                CURE.LOGGER.log(Level.FINEST, "merged {0} with {1}", new Object[]{u.getClusterId(), v.getClusterId()});

                deleteRep(u);
                deleteRep(v);
                insertRep(w);

                //assign arbitrary cluster from heap
                w.closest = heap.peek();
                updateHeap(u, v, w);
                heap.add(w);
            } catch (KeySizeException | KeyDuplicateException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        CURE.LOGGER.log(Level.INFO, "finished, heap size = {0}, k = {1}", new Object[]{heap.size(), k});
    }

    private void updateHeap(C u, C v, C w) {
        Iterator<C> iter = heap.iterator();
        C x;
        LinkedList<C> toUpdate = new LinkedList<>();
        double dist;
        while (iter.hasNext()) {
            x = iter.next();
            dist = w.dist(x, dm);
            if (dist < w.dist(w.closest, dm)) {
                w.closest = x;
                w.distClosest = dist;
            }
            if (x.closest.equals(u) || x.closest.equals(v)) {
                if (x.distClosest < dist) {
                    x.closest = closestCluster(x, x.distClosest);
                    if (x.closest == null) {
                        //we weren't able to find closer cluster
                        x.closest = w;
                        x.distClosest = dist;
                    }
                } else {
                    x.closest = w;
                    x.distClosest = dist;
                }
                toUpdate.add(x);
            } else if (dist < x.distClosest) {
                x.closest = w;
                x.distClosest = dist;
                toUpdate.add(x);
            }
        }

        for (C clust : toUpdate) {
            relocate(clust);
        }
    }

    /**
     * Any cluster might be the closest one, we have to search through all
     * points in rep and find a cluster that is closest to one of
     * representatives. But not further than maxDist.
     *
     * @param x
     * @param maxDist distance to another cluster which will be considered as
     * closest if we don't find closer cluster
     * @return null if neighbor can't be found in maxDist (Euclidean distance)
     */
    private CureCluster<E> closestCluster(C x, double maxDist) {
        double min = Double.POSITIVE_INFINITY;
        C minClust = null;
        double dist;
        C nn = null;
        int eq;
        for (E rep : x.rep) {
            eq = 1;
            //make sure nn is not in rep
            while (eq > 0) {
                eq = 0;
                nn = nearest(rep, maxDist, x.getClusterId());
                if (nn == null) {
                    break;
                }
                if (nn.getClusterId() == x.getClusterId()) {
                    eq++;
                } else {
                    for (E r1 : x.rep) {
                        //nn is same as of representatives
                        if (nn.equals(r1)) {
                            eq++;
                        }
                    }
                }
            }
            if (nn != null) {
                dist = dist(nn, rep);
                if (dist < min) {
                    min = dist;
                    minClust = nn;
                }
            }
        }
        if (minClust != null) {
            x.distClosest = min;
        }
        return minClust;
    }

    private void relocate(C x) {
        heap.remove(x);
        heap.add(x);
    }

    /**
     * Computes the min distance of a point from the group of points
     *
     * @param p Point p
     * @param cluster Group of points
     * @return double The Minimum Euclidean Distance
     */
    public double dist(C cluster, E p) {
        double minDistance = Double.POSITIVE_INFINITY;
        double distance;
        for (E q : cluster) {
            if (p.equals(q)) {
                continue;
            }
            distance = dm.measure(p, q);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        if (minDistance == Double.POSITIVE_INFINITY) {
            return 0;
        } else {
            return minDistance;
        }
    }

    /**
     * Insert all representative points of the cluster to the KD-Tree
     *
     * @param cluster Merged Cluster
     * @throws org.clueminer.kdtree.KeySizeException
     * @throws org.clueminer.kdtree.KeyDuplicateException
     */
    public void insertRep(C cluster) throws KeySizeException, KeyDuplicateException {
        for (E point : cluster.rep) {
            double[] key = point.arrayCopy();
            C res = kdtree.search(key);
            if (res == null) {
                kdtree.insert(key, cluster);
            }
        }
    }

    /**
     * Delete all representative points of the cluster from the KD Tree
     *
     * @param cluster Cluster which got merged
     */
    public void deleteRep(CureCluster<E> cluster) {
        for (E point : cluster.rep) {
            try {
                kdtree.delete(point.arrayCopy());
            } catch (KeySizeException | KeyMissingException ex) {
                //nothing to do
            }
        }
    }

    /**
     * Merge two clusters. Calculate the new representative points and shrink
     * them. Figure 6 in original paper.
     *
     * @param u Cluster 1 to be merged
     * @param v Cluster 2 to be merged
     * @return Cluster The Merged Cluster
     */
    public C merge(CureCluster<E> u, CureCluster<E> v) {
        CureCluster<E> w = createCluster(u);
        E m1 = u.getCentroid();
        E m2 = v.getCentroid();
        E mean = w.builder().build();
        for (int i = 0; i < m1.size(); i++) {
            mean.set(i, (m1.get(i) + m2.get(i)) / 2.0);
        }
        w.setCentroid(mean);

        for (E inst : u) {
            w.add(inst);
        }
        for (E inst : v) {
            w.add(inst);
        }

        CureCluster<E> tmpSet = new CureCluster<>();
        tmpSet.setAttributes(u.getAttributes());
        double minDist;
        for (int i = 0; i < numberofRepInCluster; i++) {
            double maxDist = 0;
            E maxPoint = null;
            for (E p : w) {
                if (i == 0) {
                    minDist = dm.measure(p, mean);
                } else {
                    minDist = dist((C) tmpSet, p);
                }
                if (minDist >= maxDist) {
                    maxDist = minDist;
                    maxPoint = p;
                }
            }
            if (maxPoint != null) {
                tmpSet.add(maxPoint);
            }
        }

        InstanceBuilder<E> builder = tmpSet.builder();
        for (E p : tmpSet) {
            E rep = builder.build(w.attributeCount());
            for (int j = 0; j < w.attributeCount(); j++) {
                rep.set(j, p.get(j) + shrinkFactor * (mean.get(j) - p.get(j)));
            }
            //rep.index = newPointCount++;
            rep.setIndex(CURE.incCurrentRepCount());
            w.rep.add(rep);
        }
//        clustering.remove((C) u);
//        clustering.remove((C) v);
        C cw = (C) w;
        //clustering.add(cw);
        return cw;
    }

}
