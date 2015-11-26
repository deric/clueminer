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
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.kdtree.KDTree;
import org.clueminer.kdtree.KeyDuplicateException;
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
    private KDTree<GraphCluster<E>> kdTree;

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
        for (GraphCluster<E> t : tiny) {
            try {
                E inst = t.get(0); //there's just one instance
                List<GraphCluster<E>> nn = kdTree.nearest(inst.arrayCopy(), 1);
                //TODO: check constrains
                //merge with closest cluster
                System.out.println("t = " + t.getClusterId() + " nn = " + nn.get(0).getClusterId());
                //blacklist.add(t.getClusterId());
                //blacklist.add(nn.get(0).getClusterId());
                //TODO: this is quite inefficient, though it reduces number of compared similarities
                clusters.remove(nn.get(0));
                clusters.remove(t);
                merge(t, nn.get(0), pref);
            } catch (KeySizeException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        System.out.println("after filtering: " + clusters.size());
    }

    @Override
    protected int buildQueue(int numClusters, Props pref) {
        int k = 10;
        int capacity = k * numClusters;
        System.out.println("pq capacity = " + capacity);
        pq = initQueue(capacity);
        double sim;
        //number of nearest clusters that we evaluate

        AbstractSimilarity as = (AbstractSimilarity) evaluation;
        GraphPropertyStore gps = as.getGraphPropertyStore(clusters.get(0));
        int maxClusterId = clusters.size();

        System.out.println("clusters " + numClusters);
        System.out.println("gps capacity = " + gps.getCapacity());

        for (GraphCluster<E> a : clusters) {
            //except tiny clusters
            if (a.size() > 1) {
                try {
                    //find nearest neighbors
                    List<GraphCluster<E>> nn = kdTree.nearest(a.getCentroid().arrayCopy(), k);
                    //for each NN compute their similarities
                    for (GraphCluster<E> b : nn) {
                        if (a.getClusterId() != b.getClusterId()) {
                            sim = evaluation.score(a, b, pref);
                            pq.add(new PairValue<>(a, b, sim));
                            //gps.dump();
                        }
                    }
                } catch (KeySizeException | IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        System.out.println("pq built " + pq.size());
        return maxClusterId;
    }

    /**
     * Merge cluster containing single node with a larger cluster
     *
     * @param small
     * @param large
     * @param pref
     */
    private void merge(GraphCluster<E> small, GraphCluster<E> large, Props pref) {
        ArrayList<Node<E>> clusterNodes = large.getNodes();
        Node<E> outlier = small.getNodes().get(0);
        E inst = outlier.getInstance();
        double min = Double.MAX_VALUE, dist;
        Node minNode = null;
        for (Node<E> n : large.getNodes()) {
            dist = dm.measure(inst, n.getInstance());
            if (dm.compare(dist, min)) {
                min = dist;
                minNode = n;
            }
            System.out.println("d = " + dist);
        }
        System.out.println("min distance is " + min);
        System.out.println("edge " + outlier.getId() + " -> " + minNode.getId());
        //create an extra edge connecting the outlier with original graph
        graph.addEdge(graph.getFactory().newEdge(small.getNodes().get(0), minNode, 0, min, false));

        clusterNodes.add(outlier);
        //similarity is not important in this case
        PairValue<GraphCluster<E>> curr = new PairValue<>(small, large, 0.0);
        System.out.println("a = " + small.getClusterId() + ", b = " + large.getClusterId());

        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, large.getClusterId(), bisection, pref);
        System.out.println("creating cluster " + newCluster.getClusterId() + " nodes " + clusterNodes.size());
        clusters.add(newCluster);
        evaluation.clusterCreated(curr, newCluster, pref);
        //   addIntoTree(curr, pref);
        updateExternalProperties(newCluster, small, large);
        //addIntoQueue(newCluster, pref);
    }

    private void addIntoQueue(GraphCluster<E> cluster, Props pref) {
        double sim;
        //TODO: check bounds
        for (GraphCluster<E> a : clusters) {
            if (!blacklist.contains(a.getClusterId())) {
                if (a.getClusterId() != cluster.getClusterId()) {
                    sim = evaluation.score(a, cluster, pref);
                    System.out.println("sim = " + sim);
                    pq.add(new PairValue<>(a, cluster, sim));
                }
            }
        }
    }

}
