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

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import org.clueminer.clustering.api.Cluster;
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
    private KDTree<E> kdtree;
    //number of clusters to be found
    int k;
    int numberofRepInCluster;
    double shrinkFactor;
    int newPointCount;
    private Distance dm;
    private Clustering<E, C> clustering;

    public ClusterSet(Dataset<E> dataset, int numberOfClusters, Props props, Distance dist) {
        numberofRepInCluster = props.getInt(CURE.MIN_REPRESENTATIVES, 5);
        shrinkFactor = props.getDouble(CURE.SHRINK_FACTOR, 0.5);
        dm = dist;
        cc = new CureComparator<>();
        k = numberOfClusters;

        try {
            buildKDTree(dataset);
        } catch (KeySizeException | KeyDuplicateException ex) {
            Exceptions.printStackTrace(ex);
        }
        buildHeap(dataset);
        startClustering();
    }

    private C createCluster(Dataset<E> dataset) {
        CureCluster<E> cluster = new CureCluster<>();
        //numbering for humans (start from 0)
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
     * Builds the KD Tree to store the data points
     */
    private void buildKDTree(Dataset<E> dataset) throws KeySizeException, KeyDuplicateException {
        kdtree = new KDTree(dataset.attributeCount());
        for (E instance : dataset) {
            kdtree.insert(instance.arrayCopy(), instance);
        }
    }

    /**
     * Builds the Initial Min Heap. Each point represents a cluster when the
     * algorithm begins. It creates each cluster and adds it to the heap.
     */
    private void buildHeap(Dataset<E> dataset) {
        heap = new PriorityQueue(dataset.size(), cc);
        clustering = new ClusterList<>();
        C cluster;
        E nn;
        for (E instance : dataset) {
            cluster = createCluster(dataset);
            cluster.rep.add(instance);
            cluster.add(instance);

            nn = nearest(instance, 1);
            cluster.distClosest = dm.measure(instance, nn);
            cluster.closestClusterRep = nn;
            clustering.add(cluster);
        }
        //when all instances are assigned to a cluster, update closest cluster
        for (int i = 0; i < clustering.size(); i++) {
            cluster = clustering.get(i);
            cluster.closest = clustering.assignedCluster(cluster.closestClusterRep);
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
    private E nearest(E needle, int k) {
        List<E> nn;
        E nearest = null;
        int i;
        try {
            nn = kdtree.nearest(needle.arrayCopy(), k + 1);
            i = k;
            while (i > -1) {
                nearest = nn.get(i);
                //exclude needle from the nearest neighbors
                if (nearest.getIndex() == needle.getIndex()) {
                    i--;
                } else {
                    return nearest;
                }
            }
        } catch (KeySizeException | IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
        return nearest;
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
            CURE.logger.log(Level.FINEST, "heap size = {0}", heap.size());
            try {
                //extract_min(Q)
                u = heap.remove();
                v = (C) u.closest;
                heap.remove(v);
                w = merge(u, v);
                ///CURE.logger.log(Level.INFO, "merged {0} with {1}", new Object[]{u, v});

                deleteRep(u);
                deleteRep(v);
                insertRep(w);

                w.closest = u;
                //updateHeap(u, v, w);
                heap.add(w);
                adjustHeap(w, u, v);
            } catch (KeySizeException | KeyDuplicateException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void updateHeap(C u, C v, C w) {
        Iterator<C> iter = heap.iterator();
        C x;
        while (iter.hasNext()) {
            x = iter.next();
            if (w.dist(x, dm) < w.dist(w.closest, dm)) {
                w.closest = x;
            }
            if (x.closest.equals(u) || x.closest.equals(v)) {
                if (x.dist(x.closest, dm) < x.dist(w, dm)) {
                    x.closest = closestCluster(x);
                } else {
                    x.closest = w;
                }
                relocate(x);
            } else if (x.dist(x.closest, dm) > x.dist(w, dm)) {
                x.closest = w;
                relocate(x);
            }
        }
    }

    /**
     * Any cluster might be the closest one, we have to search through all
     * points in rep
     *
     * @param x
     * @return
     */
    private CureCluster<E> closestCluster(C x) {
        double min = Double.POSITIVE_INFINITY;
        E minInst = null;
        double dist;
        E nn = null;
        int currK;
        int eq = 0;
        for (E rep : x.rep) {
            currK = 1;
            //make sure nn is not in rep
            while (eq <= (rep.size() - 1)) {
                eq = 0;
                nn = nearest(rep, currK);
                for (E r1 : x.rep) {
                    if (!nn.equals(r1)) {
                        eq++;
                    }
                }
                currK++;
            }
            dist = dm.measure(rep, nn);
            if (dist < min) {
                min = dist;
                minInst = nn;
            }
        }
        if (minInst != null) {
            return clustering.assignedCluster(minInst);
        }
        throw new RuntimeException("could not find closest cluster to " + x.toString());
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
            if (!(cluster1.closest == oldcluster1) && !(cluster1.closest == oldCluster2)) {
                heap.add(cluster1);
                continue;
            }
            cluster1.distClosest = Double.POSITIVE_INFINITY;
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) {
                    continue;
                }
                C cluster2 = clusters.get(j);
                distance = cluster1.dist(cluster2, dm);
                if (distance < cluster1.distClosest) {
                    cluster1.distClosest = distance;
                    cluster1.closest = cluster2;
                }
            }
            heap.add(cluster1);
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
            E res = kdtree.search(key);
            if (res == null) {
                kdtree.insert(key, point);
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
        for (E inst : u) {
            w.add(inst);
        }
        for (E inst : v) {
            w.add(inst);
        }
        E m1 = u.getCentroid();
        E m2 = v.getCentroid();
        E mean = w.builder().build();
        for (int i = 0; i < m1.size(); i++) {
            mean.set(i, (m1.get(i) + m2.get(i)) / 2.0);
        }
        w.setCentroid(mean);

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
        clustering.add(cw);
        return cw;
    }

}
