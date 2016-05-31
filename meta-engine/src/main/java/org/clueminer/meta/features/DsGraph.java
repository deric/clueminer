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
package org.clueminer.meta.features;

import java.util.HashMap;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.GraphConvertorFactory;
import org.clueminer.meta.api.DataStats;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class DsGraph<E extends Instance> implements DataStats<E> {

    public static final String EDGES = "edges";

    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String GRAPH_CONV = "graph_conv";

    @Override
    public String[] provides() {
        return new String[]{EDGES};
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        switch (feature) {
            case EDGES:
                return 0.0;
            default:
                throw new UnsupportedOperationException("unsupported feature: " + feature);
        }
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props props) {
        Graph graph = new AdjListGraph();
        String dist = props.get("distance", "Euclidean");
        Distance distanceFunction = DistanceFactory.getInstance().getProvider(dist);
        int iter = (int) (2 * Math.sqrt(dataset.size()));
        int maxIterations = props.getInt(MAX_ITERATIONS, iter);

        Long[] mapping = AdjListFactory.getInstance().createNodesFromInput(dataset, graph);
        String initializer = props.get(GRAPH_CONV, "k-NN");
        GraphConvertor graphCon = GraphConvertorFactory.getInstance().getProvider(initializer);
        System.out.println("using " + graphCon.getName());
        graphCon.setDistanceMeasure(distanceFunction);
        graphCon.createEdges(graph, dataset, mapping, props);

        System.out.println("nodes: " + graph.getNodeCount());
        System.out.println("edges: " + graph.getEdgeCount());

        features.put(EDGES, Double.NaN);
    }

}
