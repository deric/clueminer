/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.graph.knn;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.List;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.AbsGraphConvertor;
import static org.clueminer.graph.api.AbsGraphConvertor.DIST_TO_EDGE;
import org.clueminer.graph.api.DIST2EDGE;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeType;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphConvertor;
import static org.clueminer.graph.api.GraphConvertorFactory.INCLUDE_EDGES;
import org.clueminer.graph.api.Node;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.KnnFactory;
import static org.clueminer.neighbor.KnnFactory.KNN_SEARCH;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Shared neighbors are marked with a bi-directed edge.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = GraphConvertor.class)
public class DirectedKnnBuilder<E extends Instance> extends AbsGraphConvertor<E> implements GraphConvertor<E> {

    public static final String NAME = "bi k-NN";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Find k-nearest neighbors and add an edge between nodes
     *
     * @param graph
     * @param dataset
     * @param mapping
     * @param params
     */
    @Override
    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params) {
        KnnFactory<E> kf = KnnFactory.getInstance();
        if (!params.containsKey(KNN_SEARCH)) {
            params.put(KNN_SEARCH, "linear k-nn");
        }
        KNNSearch<E> alg = kf.getProvider(params.get(KNN_SEARCH));
        if (alg == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        String includeEdges = params.get(INCLUDE_EDGES, "any");
        alg.setDataset(dataset);
        alg.setDistanceMeasure(dm);
        int k = params.getInt("k", params.getInt("chameleon.k", 5));

        switch (includeEdges) {
            case "bidirect":
                buildBiGraph(k, graph, alg, mapping, params);
                break;
            default:
                buildMixedGraph(k, graph, alg, mapping, params);
        }
    }

    public Table<Node, Node, Edge> newTable() {
        return Tables.newCustomTable(
                Maps.<Node, Map<Node, Edge>>newHashMap(),
                new Supplier<Map<Node, Edge>>() {
            @Override
            public Map<Node, Edge> get() {
                return Maps.newHashMap();
            }
        });
    }

    /**
     * Will include only bidirected edges.
     *
     * @param k
     * @param graph
     * @param alg
     * @param mapping
     * @param params
     */
    private void buildBiGraph(int k, Graph graph, KNNSearch<E> alg, Long[] mapping, Props params) {
        Neighbor[] nn;
        long nodeId;
        Node<E> target;
        Edge edge;
        DIST2EDGE methd = DIST2EDGE.valueOf(params.get(DIST_TO_EDGE, "INVERSE"));
        E inst;
        GraphBuilder factory = graph.getFactory();
        Table<Node, Node, Edge> cache = newTable();
        Node<E> a, b;
        for (Node<E> node : graph.getNodes()) {
            inst = node.getInstance();
            nn = alg.knn(inst, k, params);
            for (Neighbor neighbor : nn) {
                nodeId = mapping[neighbor.index];
                target = graph.getNode(nodeId);
                //check if inverse edge already exits
                if (target.getId() < node.getId()) {
                    a = target;
                    b = node;
                } else {
                    a = node;
                    b = target;
                }
                edge = cache.get(a, b);
                if (edge != null) {
                    edge.setDirection(EdgeType.BOTH);
                    //add only bidirect edges
                    graph.addEdge(edge);
                    cache.remove(a, b);
                } else {
                    edge = factory.newEdge(node, target, 1, convertDistance(neighbor.distance, methd), true);
                    cache.put(a, b, edge);
                }
            }
        }
    }

    private void buildMixedGraph(int k, Graph graph, KNNSearch<E> alg, Long[] mapping, Props params) {
        Neighbor[] nn;
        long nodeId;
        Node<E> target;
        Edge edge;
        DIST2EDGE methd = DIST2EDGE.valueOf(params.get(DIST_TO_EDGE, "INVERSE"));
        E inst;
        GraphBuilder factory = graph.getFactory();
        for (Node<E> node : graph.getNodes()) {
            inst = node.getInstance();
            nn = alg.knn(inst, k, params);
            for (Neighbor neighbor : nn) {
                nodeId = mapping[neighbor.index];
                target = graph.getNode(nodeId);
                //check if inverse edge already exits
                edge = graph.getEdge(target, node);
                if (edge != null) {
                    edge.setDirection(EdgeType.BOTH);
                } else {
                    edge = factory.newEdge(node, target, 1, convertDistance(neighbor.distance, methd), true);
                    graph.addEdge(edge);
                }
            }
        }
    }

    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params) {
        buildGraph(graph, dataset, params, null);
    }

    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params, List<E> noise) {
        GraphBuilder gb = graph.getFactory();
        Long[] mapping = gb.createNodesFromInput(dataset, graph);
        createEdges(graph, dataset, mapping, params);
    }

}
