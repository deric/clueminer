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
package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.kdtree.KDTree;
import org.clueminer.kdtree.KeyDuplicateException;
import org.clueminer.kdtree.KeySizeException;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Merger.class)
public class KnnMerger<E extends Instance> extends FastMerger<E> implements Merger<E> {

    public static final String name = "k-NN merger";

    @Override
    public String getName() {
        return name;
    }


    @Override
    public void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props pref) {
        System.out.println("input clusters: " + clusters.size());
        //build kd-tree for fast search
        kdTree = new KDTree<>(clusters.get(0).attributeCount());
        for (GraphCluster<E> a : clusters) {
            try {
                kdTree.insert(a.getCentroid().arrayCopy(), a);
            } catch (KeySizeException | KeyDuplicateException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        //renumberClusters(clusters, noise);
    }

    @Override
    public void finalize(Clustering<E, GraphCluster<E>> clusters, PriorityQueue<PairValue<GraphCluster<E>>> pq) {
        int i, j;
        PairValue<GraphCluster<E>> curr;
        Cluster<E> noise = clusters.getNoise();
        System.out.println("original clusters " + clusters.size() + " nodes " + nodes.length);

        if (nodes[nodes.length - 1] == null) {
            System.out.println("no noisy tree node, adding " + noise.size());
            List<E> n = new ArrayList<>(noise.size());
            nodes[nodes.length - 1] = new DClusterLeaf(noise.size() + 10, n);
            nodes[nodes.length - 1].setHeight(0.0);
            nodes[nodes.length - 1].setLevel(0);
        }
        List<E> treeNoise = ((DClusterLeaf) nodes[nodes.length - 1]).getInstances();
        System.out.println("noise in tree: " + treeNoise.size());
        int k = 0;
        int m = 0;
        //int numClusters = clusters.size();
        while (!pq.isEmpty()) {
            curr = pq.poll();
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
            if (!blacklist.contains(i) && !blacklist.contains(j)) {
                blacklist.add(i);
                blacklist.add(j);
                if (i == j) {
                    throw new RuntimeException("Cannot merge two same clusters");
                }
                addToNoise(noise, treeNoise, curr.A);
                addToNoise(noise, treeNoise, curr.B);
                k += 2;
            }
            m++;
        }
        if (nodes.length > clusters.size()) {
            System.out.println("shrink from " + nodes.length + " -> " + clusters.size());
            DendroNode[] shrinkNodes = new DendroNode[clusters.size()];
            System.arraycopy(nodes, 0, shrinkNodes, 0, clusters.size());
            shrinkNodes[shrinkNodes.length - 1] = nodes[nodes.length - 1];
            nodes = shrinkNodes;
        }

        if (noise.isEmpty()) {
            System.out.println("noise empty, removin' treenode");
            nodes[nodes.length - 1] = null;
        }

        /* System.out.println("nodes:");
        for (int l = 0; l < nodes.length; l++) {
            System.out.println("node " + l + ": " + nodes[l]);
        }*/
        System.out.println("root: " + nodes[nodes.length - 2]);
        System.out.println("cluster size: " + clusters.size());

    }

    private void addToNoise(Cluster<E> noise, List<E> treeNoise, GraphCluster<E> cluster) {
        for (E inst : cluster) {
            noise.add(inst);
            treeNoise.add(inst);
        }
    }

    /**
     * Merges two most similar clusters
     *
     * @param curr
     * @param pref
     * @param newClusterId
     */
    @Override
    protected void singleMerge(PairValue<GraphCluster<E>> curr, Props pref, int newClusterId) {
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (!pq.isEmpty() && (blacklist.contains(i) || blacklist.contains(j))) {
            curr = pq.poll();
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
        }

        double sigmaA = curr.A.getSigma(pref);
        double sigmaB = curr.B.getSigma(pref);
        //System.out.println("sigma(A)= " + sigmaA + ", sigma(B)= " + sigmaB);
        //double sim = clusterDist(curr.A, curr.B, pref);
        double sim = curr.getValue();
        //System.out.println("dist between clusters = " + dist);
        int l1 = nodes[i].level();
        int l2 = nodes[j].level();

        if (l1 > 0 && l2 > 0) {
            merge(i, j, curr, pref, newClusterId);
        } else if (sim > sigmaA && sim > sigmaB) {
            merge(i, j, curr, pref, newClusterId);
        } else {
            System.out.println("rejected sigmaA = " + sigmaA + ", " + sigmaB + " sim= " + sim);
            Cluster<E> noise = clusters.getNoise();
            if (l1 == 0) {
                System.out.println("l1 at level 0, cluster = " + i);
                blacklist.add(i);
                for (E node : curr.A) {
                    noise.add(node);
                }
            }

            if (l2 == 0) {
                System.out.println("l2 at level 0, cluster = " + j);
                blacklist.add(j);
                for (E node : curr.B) {
                    noise.add(node);
                }
            }

            System.out.println("noise size " + noise.size());
        }
    }

    private void merge(int i, int j, PairValue<GraphCluster<E>> curr, Props pref, int newClusterId) {
        blacklist.add(i);
        blacklist.add(j);
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        //System.out.println("merging: " + curr.getValue() + " A: " + curr.A.getClusterId() + " B: " + curr.B.getClusterId() + " -> " + newClusterId);
        //clonning won't be necessary if we don't wanna recompute RCL for clusters that were merged
        //LinkedList<Node> clusterNodes = (LinkedList<Node>) curr.A.getNodes().clone();
        //WARNING: we copy nodes from previous clusters (we save memory, but
        //it's not a good idea to work with merged clusters)
        ArrayList<Node<E>> clusterNodes = curr.A.getNodes();
        clusterNodes.addAll(curr.B.getNodes());
        merged(curr);
        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, newClusterId, bisection, pref);
        clusters.add(newCluster);
        evaluation.clusterCreated(curr, newCluster, pref);
        addIntoTree(curr, pref);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue(newCluster, pref);
    }

}
