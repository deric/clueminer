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
import org.clueminer.clustering.api.Clustering;
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
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Merger.class)
public class FastMerger2<E extends Instance> extends FastMerger<E> implements Merger<E> {

    public static final String name = "fast merger 2";
    private KDTree<GraphCluster<E>> kdTree;

    @Override
    public String getName() {
        return name;
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

        if (dist < sigmaA && dist < sigmaB) {
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
        } else {
            System.out.println("rejected sigmaA = " + sigmaA + ", " + sigmaB + " dist= " + dist);
        }
    }

}
