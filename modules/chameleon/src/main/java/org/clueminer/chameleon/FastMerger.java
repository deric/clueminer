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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.similarity.AbstractSimilarity;
import org.clueminer.chameleon.similarity.CLS;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.kdtree.KDTree;
import org.clueminer.kdtree.KeyDuplicateException;
import org.clueminer.kdtree.KeyMissingException;
import org.clueminer.kdtree.KeySizeException;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * An experimental merger without necessity of merging O(n^2) items
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Merger.class)
public class FastMerger<E extends Instance> extends PairMerger<E> implements Merger<E> {

    public static final String name = "fast merger";
    protected KDTree<GraphCluster<E>> kdTree;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<E> initialize(ArrayList<ArrayList<Node<E>>> clusterList, Graph<E> graph, Bisection bisection, Props params, ArrayList<E> noise) {
        this.graph = graph;
        this.bisection = bisection;
        blacklist = new HashSet<>();
        clusters = createClusters(clusterList, bisection, params);
        assignNodesToCluters(clusters);
        computeExternalProperties(clusters);
        if (noise == null) {
            noise = new ArrayList<>();
        }
        prefilter(clusters, noise, params);
        nodes = initiateTree(clusters, noise);
        return noise;
    }

    @Override
    public void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props pref) {
        System.out.println("input clusters: " + clusters.size());
        //build kd-tree for fast search
        kdTree = new KDTree<>(clusters.get(0).attributeCount());
        List<GraphCluster<E>> tiny = new LinkedList<>();
        for (GraphCluster<E> a : clusters) {
            try {
                //for small clusters we can't resonably compute similarity
                if (a.size() == 1) {
                    tiny.add(a);
                } else {
                    kdTree.insert(a.getCentroid().arrayCopy(), a);
                }
            } catch (KeySizeException | KeyDuplicateException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        //get rid of tiny clusters
        System.out.println("found " + tiny.size() + " tiny cluster");
        GraphCluster<E> closest;
        for (GraphCluster<E> t : tiny) {
            try {
                E inst = t.get(0); //there's just one instance
                List<GraphCluster<E>> nn = kdTree.nearest(inst.arrayCopy(), 1);
                //TODO: check constrains
                //merge with closest cluster
                closest = nn.get(0);
                System.out.println("t = " + t.getClusterId() + " nn = " + closest.getClusterId());
                //blacklist.add(t.getClusterId());
                //blacklist.add(nn.get(0).getClusterId());
                //TODO: this is quite inefficient, though it reduces number of compared similarities
                clusters.remove(closest);
                clusters.remove(t);
                merge(t, closest, noise, pref);
            } catch (KeySizeException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        System.out.println("after filtering: " + clusters.size() + ", noise: " + noise.size());
        renumberClusters(clusters, noise);
    }

    /**
     * Initialize cluster pairs based on its nearest-neighbors. We don't compute
     * full similarity matrix.
     *
     * @param numClusters
     * @param pref
     * @return
     */
    @Override
    protected int buildQueue(int numClusters, Props pref) {
        int k = 15;
        int capacity = k * numClusters;
        System.out.println("pq capacity = " + capacity);
        pq = initQueue(capacity);
        double sim;
        //number of nearest clusters that we evaluate

        AbstractSimilarity as = (AbstractSimilarity) evaluation;
        GraphPropertyStore gps = as.getGraphPropertyStore(clusters.get(0));
        int maxClusterId = clusters.size();
        CLS<E> closeness = new CLS<>();

        System.out.println("clusters " + numClusters);
        System.out.println("gps capacity = " + gps.getCapacity());

        E centroid;
        for (GraphCluster<E> a : clusters) {
            try {
                //find nearest neighbors
                centroid = a.getCentroid();
                if (centroid == null) {
                    throw new RuntimeException("no centroid of cluster " + a.toString());
                }
                List<GraphCluster<E>> nn = kdTree.nearest(centroid.arrayCopy(), k);
                //for each NN compute their similarities
                for (GraphCluster<E> b : nn) {
                    if (a.getClusterId() != b.getClusterId()) {
                        sim = evaluation.score(a, b, pref);
                        if (sim > 0) {
                            pq.add(new PairValue<>(a, b, sim));
                        } else {
                            sim = closeness.score(a, b, pref);
                            if (sim > 0) {
                                System.out.println("CLS (" + a.getClusterId() + "," + b.getClusterId() + ") = " + sim);
                                pq.add(new PairValue<>(a, b, sim));
                            }
                            //System.out.println("excluding pair " + a + ", " + b);
                        }
                        //gps.dump();
                    }
                }
            } catch (KeySizeException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        System.out.println("pq built " + pq.size());
        return maxClusterId;
    }

    /**
     * Merge cluster containing single node with a larger cluster (this merge is
     * not reflected in the dendrogram)
     *
     * @param small
     * @param large
     * @param noise
     * @param pref
     * @return
     */
    protected GraphCluster<E> merge(GraphCluster<E> small, GraphCluster<E> large, ArrayList<E> noise, Props pref) {
        ArrayList<Node<E>> clusterNodes = large.getNodes();
        Node<E> outlier = small.getNodes().get(0);
        E inst = outlier.getInstance();
        double min = Double.MAX_VALUE, dist;
        Node minNode = null;
        for (Node<E> n : large.getNodes()) {
            dist = dm.measure(inst, n.getInstance());
            if (dm.compare(dist, min) && (dist < large.getSigma(pref))) {
                min = dist;
                minNode = n;
            }
        }

        if (minNode != null) {
            try {
                System.out.println("min distance is " + min);
                System.out.println("sigma dist = " + large.getSigma(pref));
                System.out.println("edge " + outlier.getId() + " -> " + minNode.getId());
                //create an extra edge connecting the outlier with original graph
                graph.addEdge(graph.getFactory().newEdge(small.getNodes().get(0), minNode, 0, min, false));

                clusterNodes.add(outlier);
                //similarity is not important in this case
                PairValue<GraphCluster<E>> curr = new PairValue<>(small, large, 0.0);

                GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, large.getClusterId(), bisection, pref);
                System.out.println("adding to cluster cluster " + newCluster.getClusterId() + " nodes " + clusterNodes.size());
                clusters.add(newCluster);
                evaluation.clusterCreated(curr, newCluster, pref);
                //   addIntoTree(curr, pref);
                updateExternalProperties(newCluster, small, large);

                kdTree.delete(large.getCentroid().arrayCopy());
                kdTree.insert(newCluster.getCentroid().arrayCopy(), newCluster);
                //addIntoQueue(newCluster, pref);
                return newCluster;
            } catch (KeySizeException | KeyMissingException | KeyDuplicateException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        noise.add(inst);
        return null;
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
        //System.out.println("sigma dist(A)= " + sigmaA);
        //System.out.println("sigma dist(B)= " + sigmaB);
        double dist = dm.measure(curr.A.getCentroid(), curr.B.getCentroid());
        //System.out.println("dist between clusters = " + dist);

//        if (dist < sigmaA && dist < sigmaB) {
        blacklist.add(i);
        blacklist.add(j);
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        //System.out.println("merging: " + curr.getValue() + " A: " + curr.A.getClusterId() + " B: " + curr.B.getClusterId());
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
        /*      } else {
         System.out.println("rejected sigmaA = " + sigmaA + ", " + sigmaB + " dist= " + dist);
         }*/
    }

    @Override
    protected void merged(PairValue<GraphCluster<E>> curr) {
        try {
            kdTree.delete(curr.A.getCentroid().arrayCopy());
            kdTree.delete(curr.B.getCentroid().arrayCopy());
        } catch (KeySizeException | KeyMissingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void addIntoQueue(GraphCluster<E> cluster, Props pref) {
        double sim;

        try {
            List<GraphCluster<E>> nn = kdTree.nearest(cluster.getCentroid().arrayCopy(), 10);
            for (GraphCluster<E> b : nn) {
                if (cluster.getClusterId() != b.getClusterId()) {
                    sim = evaluation.score(cluster, b, pref);
                    //if (sim > 0) {
                    pq.add(new PairValue<>(cluster, b, sim));
                    //}
                }
            }

        } catch (KeySizeException | IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            //insert newly created cluster to kd-tree
            kdTree.insert(cluster.getCentroid().arrayCopy(), cluster);
        } catch (KeySizeException | KeyDuplicateException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
