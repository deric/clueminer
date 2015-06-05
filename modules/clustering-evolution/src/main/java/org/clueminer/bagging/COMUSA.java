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
package org.clueminer.bagging;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.PriorityQueue;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Consensus;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.DoubleElem;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Evidence accumulation using a basic weighted graph
 *
 * S. Mimaroglu and E. Erdil, “Obtaining Better Quality Final Clustering by
 * Merging a Collection of Clusterings,” Bioinformatics, vol. 26, pp. 2645-2646,
 * 2010.
 *
 * @author deric
 */
@ServiceProvider(service = Consensus.class)
public class COMUSA implements Consensus {

    public static final String name = "COMUSA";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<? extends Cluster> reduce(Clustering[] clusts, AbstractClusteringAlgorithm alg, ColorGenerator cg, Props props) {

        Clustering c = clusts[0];
        //total number of items
        int n = c.instancesCount();

        Graph graph = new AdjListGraph();
        Object2LongOpenHashMap<Instance> mapping = new Object2LongOpenHashMap();

        Instance a, b;
        Node na, nb;
        //cluster membership
        int ca, cb;
        int x = 0;
        Edge edge;
        //accumulate evidence
        for (Clustering clust : clusts) {
            System.out.println("reducing " + (x++));
            for (int i = 1; i < n; i++) {
                a = clust.instance(i);
                na = fetchNode(graph, mapping, a);
                ca = clust.assignedCluster(a.getIndex());
                for (int j = 0; j < i; j++) {
                    b = clust.instance(j);
                    nb = fetchNode(graph, mapping, b);
                    //for each pair of instances check if placed in the same cluster
                    cb = clust.assignedCluster(b.getIndex());
                    if (ca == cb) {
                        edge = graph.getEdge(na, nb);
                        //check if exists
                        if (edge == null) {
                            edge = graph.getFactory().newEdge(na, nb, 0, 0, false);
                            graph.addEdge(edge);
                        }
                        //increase weight by 1
                        edge.setWeight(edge.getWeight() + 1.0);
                    }
                }
            }
        }

        //degree of freedom
        double df;
        double w, attain;
        EdgeIterable neigh;
        PriorityQueue<DoubleElem> pq = new PriorityQueue<>(graph.getNodeCount());
        DoubleElem<Node> elem;
        //for each node compute attainment score
        for (Node node : graph.getNodes()) {
            neigh = graph.getEdges(node);
            df = neigh.size();
            w = 0.0;
            for (Edge ne : neigh) {
                w += ne.getWeight();
            }
            attain = w / df;
            elem = new DoubleElem<>(node, attain);
            pq.add(elem);
        }

        ObjectOpenHashSet<Node> blacklist = new ObjectOpenHashSet();
        Node node;
        while (!pq.isEmpty()) {
            elem = pq.poll();
            node = elem.getElem();
            if (!blacklist.contains(node)) {
                blacklist.add(node);
                System.out.println("w: " + elem.getValue());
            }
        }
        //number of clusters is just a hint
        int k = props.getInt(KMeans.K);
        Clustering<? extends Cluster> result = new ClusterList(k);


        return result;
    }

    private Node createNode(Graph g, Object2LongOpenHashMap<Instance> mapping, Instance inst) {
        Node node = g.getFactory().newNode(inst);
        mapping.put(inst, node.getId());
        g.addNode(node);
        return node;
    }

    private Node fetchNode(Graph g, Object2LongOpenHashMap<Instance> mapping, Instance inst) {
        if (mapping.containsKey(inst)) {
            return g.getNode(mapping.getLong(inst));
        }
        return createNode(g, mapping, inst);
    }

}
