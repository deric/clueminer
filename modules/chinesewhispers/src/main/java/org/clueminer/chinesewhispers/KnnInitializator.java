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
package org.clueminer.chinesewhispers;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.distance.api.KNN;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = GraphConvertor.class)
public class KnnInitializator implements GraphConvertor {

    private DistanceMeasure dm;
    private static final String name = "k-NN";

    @Override
    public String getName() {
        return name;
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
    public void createEdges(Graph graph, Dataset<? extends Instance> dataset, Long[] mapping, Props params) {
        KNN knn = Lookup.getDefault().lookup(KNN.class);
        if (knn == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        int k = params.getInt("k", 5);
        int[] nn;
        long nodeId;
        Node target;
        Edge edge;
        for (Node node : graph.getNodes()) {
            nn = knn.nnIds(node.getInstance().getIndex(), k, dataset, params);
            for (int i = 0; i < nn.length; i++) {
                nodeId = mapping[nn[i]];
                target = graph.getNode(nodeId);
                edge = graph.getFactory().newEdge(node, target);
                graph.addEdge(edge);
            }
        }
    }

    @Override
    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

}
