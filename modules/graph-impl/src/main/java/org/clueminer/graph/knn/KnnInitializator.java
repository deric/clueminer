/*
 * Copyright (C) 2011-2016 clueminer.org
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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
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
public class KnnInitializator<E extends Instance> implements GraphConvertor<E> {

    private Distance dm;
    private static final String NAME = "k-NNG";

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
            params.put(KNN_SEARCH, "KD-tree");
        }
        KNNSearch<E> alg = kf.getProvider(params.get(KNN_SEARCH));
        if (alg == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        alg.setDataset(dataset);
        alg.setDistanceMeasure(dm);
        int k = params.getInt("k", 5);
        Neighbor[] nn;
        long nodeId;
        Node<E> target;
        Edge edge;
        for (Node<E> node : graph.getNodes()) {
            nn = alg.knn(node.getInstance(), k, params);
            for (Neighbor neighbor : nn) {
                nodeId = mapping[neighbor.index];
                target = graph.getNode(nodeId);
                edge = graph.getFactory().newEdge(node, target);
                graph.addEdge(edge);
            }
        }
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    @Override
    public Distance getDistanceMeasure() {
        return dm;
    }

}
