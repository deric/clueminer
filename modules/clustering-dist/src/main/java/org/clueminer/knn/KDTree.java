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
package org.clueminer.knn;

import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.NearestNeighborSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.sort.HeapSelect;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 */
public class KDTree<E extends Instance> extends AbstractKNN<E> implements NearestNeighborSearch<E>, KNNSearch<E>, RNNSearch<E> {

    public static final String name = "KD-tree";

    /**
     * The root node of KD-Tree.
     */
    private KDNode root;
    /**
     * The index of objects in each nodes.
     */
    private int[] index;

    /**
     * Constructor.
     *
     * @param dataset
     */
    public KDTree(Dataset<E> dataset) {
        this.dataset = dataset;
        this.dm = EuclideanDistance.getInstance();
        ((EuclideanDistance) dm).setSqrt(false);
        buildTree();
    }

    public KDTree() {
        this.dm = new EuclideanDistance();
        ((EuclideanDistance) dm).setSqrt(false);
    }

    private void buildTree() {
        if (dataset == null) {
            throw new RuntimeException("missing dataset");
        }
        if (dataset.isEmpty()) {
            throw new RuntimeException("can't build kd-tree from an empty dataset");
        }
        if (dataset.attributeCount() == 0) {
            throw new RuntimeException("Dataset doesn't have any attributes");
        }
        int n = dataset.size();
        index = new int[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        // Build the tree
        root = buildNode(0, n);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Build a k-d tree from the given set of dataset.
     */
    private KDNode buildNode(int begin, int end) {
        int d = dataset.attributeCount();

        // Allocate the node
        KDNode node = new KDNode();

        // Fill in basic info
        node.count = end - begin;
        node.index = begin;

        // Calculate the bounding box
        double[] lowerBound = new double[d];
        double[] upperBound = new double[d];

        for (int i = 0; i < d; i++) {
            lowerBound[i] = dataset.get(index[begin], i);
            upperBound[i] = dataset.get(index[begin], i);
        }

        for (int i = begin + 1; i < end; i++) {
            for (int j = 0; j < d; j++) {
                double c = dataset.get(index[i], j);
                if (lowerBound[j] > c) {
                    lowerBound[j] = c;
                }
                if (upperBound[j] < c) {
                    upperBound[j] = c;
                }
            }
        }

        // Calculate bounding box stats
        double maxRadius = -1;
        for (int i = 0; i < d; i++) {
            double radius = (upperBound[i] - lowerBound[i]) / 2;
            if (radius > maxRadius) {
                maxRadius = radius;
                node.split = i;
                node.cutoff = (upperBound[i] + lowerBound[i]) / 2;
            }
        }

        // If the max spread is 0, make this a leaf node
        if (maxRadius == 0) {
            node.lower = node.upper = null;
            return node;
        }

        // Partition the dataset around the midpoint in this dimension. The
        // partitioning is done in-place by iterating from left-to-right and
        // right-to-left in the same way that partioning is done in quicksort.
        int i1 = begin, i2 = end - 1, size = 0;
        while (i1 <= i2) {
            boolean i1Good = (dataset.get(index[i1], node.split) < node.cutoff);
            boolean i2Good = (dataset.get(index[i2], node.split) >= node.cutoff);

            if (!i1Good && !i2Good) {
                int temp = index[i1];
                index[i1] = index[i2];
                index[i2] = temp;
                i1Good = i2Good = true;
            }

            if (i1Good) {
                i1++;
                size++;
            }

            if (i2Good) {
                i2--;
            }
        }

        // Create the child nodes
        node.lower = buildNode(begin, begin + size);
        node.upper = buildNode(begin + size, end);

        return node;
    }

    /**
     * Returns the nearest neighbors of the given target starting from the give
     * tree node.
     *
     * @param q the query key.
     * @param node the root of subtree.
     * @param neighbor the current nearest neighbor.
     */
    private void search(E q, KDNode node, Neighbor<E> neighbor) {
        if (node.isLeaf()) {
            // look at all the instances in this leaf
            for (int idx = node.index; idx < node.index + node.count; idx++) {
                if (q == dataset.get(index[idx]) && identicalExcluded) {
                    continue;
                }
                //TODO: squared distance would be enough
                double distance = dm.measure(q, dataset.get(index[idx]));
                if (distance < neighbor.distance) {
                    neighbor.key = dataset.get(index[idx]);
                    neighbor.index = index[idx];
                    neighbor.distance = distance;
                }
            }
        } else {
            KDNode nearer, further;
            double diff = q.get(node.split) - node.cutoff;
            if (diff < 0) {
                nearer = node.lower;
                further = node.upper;
            } else {
                nearer = node.upper;
                further = node.lower;
            }

            search(q, nearer, neighbor);

            // now look in further half
            if (neighbor.distance >= diff * diff) {
                search(q, further, neighbor);
            }
        }
    }

    /**
     * Returns (in the supplied heap object) the k nearest neighbors of the
     * given target starting from the give tree node.
     *
     * @param q the query key.
     * @param node the root of subtree.
     * @param k the number of neighbors to find.
     * @param heap the heap object to store/update the kNNs found during the
     * search.
     */
    private void search(E q, KDNode node, HeapSelect<Neighbor<E>> heap) {
        if (node.isLeaf()) {
            // look at all the instances in this leaf
            for (int idx = node.index; idx < node.index + node.count; idx++) {
                if (q == dataset.get(index[idx]) && identicalExcluded) {
                    continue;
                }

                //TODO: squared distance would be enough
                double distance = dm.measure(q, dataset.get(index[idx]));
                Neighbor<E> datum = heap.peek();
                if (distance < datum.distance) {
                    datum.distance = distance;
                    datum.index = index[idx];
                    datum.key = dataset.get(index[idx]);
                    heap.heapify();
                }
            }
        } else {
            KDNode nearer, further;
            double diff = q.get(node.split) - node.cutoff;
            if (diff < 0) {
                nearer = node.lower;
                further = node.upper;
            } else {
                nearer = node.upper;
                further = node.lower;
            }

            search(q, nearer, heap);

            // now look in further half
            if (heap.peek().distance >= diff * diff) {
                search(q, further, heap);
            }
        }
    }

    /**
     * Returns the neighbors in the given range of search target from the give
     * tree node.
     *
     * @param q the query key.
     * @param node the root of subtree.
     * @param radius	the radius of search range from target.
     * @param neighbors the list of found neighbors in the range.
     */
    private void search(E q, KDNode node, double radius, List<Neighbor<E>> neighbors) {
        if (node.isLeaf()) {
            // look at all the instances in this leaf
            for (int idx = node.index; idx < node.index + node.count; idx++) {
                if (q == dataset.get(index[idx]) && identicalExcluded) {
                    continue;
                }

                double distance = dm.measure(q, dataset.get(index[idx]));
                if (distance <= radius) {
                    neighbors.add(new Neighbor<>(dataset.get(index[idx]), index[idx], distance));
                }
            }
        } else {
            KDNode nearer, further;
            double diff = q.get(node.split) - node.cutoff;
            if (diff < 0) {
                nearer = node.lower;
                further = node.upper;
            } else {
                nearer = node.upper;
                further = node.lower;
            }

            search(q, nearer, radius, neighbors);

            // now look in further half
            if (radius >= diff * diff) {
                search(q, further, radius, neighbors);
            }
        }
    }

    @Override
    public Neighbor<E> nearest(E q) {
        Neighbor<E> neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        search(q, root, neighbor);
        neighbor.distance = Math.sqrt(neighbor.distance);
        return neighbor;
    }

    @Override
    public Neighbor<E>[] knn(E q, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("Invalid k: " + k);
        }

        if (k > dataset.size()) {
            throw new IllegalArgumentException("Neighbor array length is larger than the dataset size");
        }

        Neighbor<E> neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        @SuppressWarnings("unchecked")
        Neighbor<E>[] neighbors = (Neighbor<E>[]) java.lang.reflect.Array.newInstance(neighbor.getClass(), k);
        HeapSelect<Neighbor<E>> heap = new HeapSelect<>(neighbors);
        for (int i = 0; i < k; i++) {
            heap.add(neighbor);
            neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        }

        search(q, root, heap);
        heap.sort();
        for (Neighbor<E> neighbor1 : neighbors) {
            neighbor1.distance = Math.sqrt(neighbor1.distance);
        }

        return neighbors;
    }

    @Override
    public void range(E q, double radius, List<Neighbor<E>> neighbors) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Invalid radius: " + radius);
        }

        search(q, root, radius, neighbors);
    }

    @Override
    public Neighbor[] knn(E q, int k, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
        buildTree();
    }

    public void delete(E q) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insert(E q, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "KD-Tree";
    }

}
