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
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chameleon.similarity.AbstractSimilarity;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.kdtree.KDTree;
import org.clueminer.kdtree.KeyDuplicateException;
import org.clueminer.kdtree.KeySizeException;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void buildQueue(int numClusters, Props pref) {
        int capacity = numClusters * numClusters;
        System.out.println("pq capacity = " + capacity);
        pq = initQueue(capacity);
        double sim;
        //number of nearest clusters that we evaluate
        int k = 3;

        AbstractSimilarity as = (AbstractSimilarity) evaluation;
        GraphPropertyStore gps = as.getGraphPropertyStore(clusters.get(0));

        System.out.println("clusters " + numClusters);
        System.out.println("gps capacity = " + gps.getCapacity());

        //build kd-tree for fast search
        KDTree<GraphCluster<E>> kdTree = new KDTree<>(clusters.get(0).attributeCount());
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
                merge(t, nn.get(0), pref);
            } catch (KeySizeException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

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
    }

    private void merge(GraphCluster<E> a, GraphCluster<E> b, Props pref) {
        ArrayList<Node<E>> clusterNodes = a.getNodes();
        clusterNodes.addAll(b.getNodes());
        //similarity is not important in this case
        PairValue<GraphCluster<E>> curr = new PairValue<>(a, b, 0.0);
        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusters.size(), bisection, pref);
        clusters.add(newCluster);
        evaluation.clusterCreated(curr, newCluster, pref);
        addIntoTree(curr, pref);
        updateExternalProperties(newCluster, a, b);
        addIntoQueue(newCluster, pref);
    }

    private void addIntoQueue(GraphCluster<E> cluster, Props pref) {
        double sim;
        GraphCluster a;
        //TODO: check bounds
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                a = clusters.get(i);
                sim = evaluation.score(a, cluster, pref);
                pq.add(new PairValue<GraphCluster<E>>(a, cluster, sim));
            }
        }
    }

}
