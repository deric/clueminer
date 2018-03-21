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

import java.util.HashSet;
import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.AbsGraphConvertor;
import org.clueminer.graph.api.DIST2EDGE;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.KnnFactory;
import static org.clueminer.neighbor.KnnFactory.KNN_SEARCH;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Construct Nearest neighbor graph
 *
 * @author Hamster
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = GraphConvertor.class)
public class KnnInitializator<E extends Instance> extends AbsGraphConvertor<E> implements GraphConvertor<E> {

    public static final String NAME = "k-NNG";

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
        alg.setDataset(dataset);
        alg.setDistanceMeasure(dm);
        int k = params.getInt("k", params.getInt("chameleon.k", 5));
        Neighbor[] nn;
        long nodeId;
        Node<E> target;
        Edge edge;
        DIST2EDGE methd = DIST2EDGE.valueOf(params.get(DIST_TO_EDGE, "INVERSE"));
        E inst;
        for (Node<E> node : graph.getNodes()) {
            inst = node.getInstance();
            nn = alg.knn(inst, k, params);
            for (Neighbor neighbor : nn) {
                nodeId = mapping[neighbor.index];
                target = graph.getNode(nodeId);
                edge = graph.getFactory().newEdge(node, target, 1, convertDistance(neighbor.distance, methd), false);
                graph.addEdge(edge);
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
        Long[] mapping;
        if (noise != null) {
            HashSet<Integer> hash = new HashSet<>(noise.size());
            for (E inst : noise) {
                hash.add(inst.getIndex());
            }

            mapping = gb.createNodesFromInput(dataset, graph, hash);
        } else {
            mapping = gb.createNodesFromInput(dataset, graph);
        }

        createEdges(graph, dataset, mapping, params);
    }

}
