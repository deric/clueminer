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
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.KnnFactory;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = DataStats.class)
public class DsGraph<E extends Instance> implements DataStats<E> {

    public static final String LOG_EDGES = "log_edges";
    public static final String EDGES_RATIO = "edges_ratio";
    public static final String AVG_FOURTH_NN = "avg_4th_nn";

    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String GRAPH_CONV = "graph_conv";

    @Override
    public String[] provides() {
        return new String[]{LOG_EDGES};
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        HashMap<String, Double> features = new HashMap<>();
        computeAll(dataset, features, params);
        switch (feature) {
            case LOG_EDGES:
                return features.get(LOG_EDGES);
            case EDGES_RATIO:
                return features.get(EDGES_RATIO);
            default:
                throw new UnsupportedOperationException("unsupported feature: " + feature);
        }
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props props) {
        Graph graph = new AdjListGraph();
        String dist = props.get("distance", "Euclidean");
        Distance distanceFunction = DistanceFactory.getInstance().getProvider(dist);

        Long[] mapping = AdjListFactory.getInstance().createNodesFromInput(dataset, graph);
        String initializer = props.get(GRAPH_CONV, "k-NNG");
        GraphConvertor graphCon = GraphConvertorFactory.getInstance().getProvider(initializer);
        graphCon.setDistanceMeasure(distanceFunction);
        graphCon.createEdges(graph, dataset, mapping, props);

        features.put(LOG_EDGES, Math.log(graph.getEdgeCount()));
        /**
         * when edges share neighbors no extra edge is added
         */
        features.put(EDGES_RATIO, graph.getEdgeCount() / (double) graph.getNodeCount());

        avg4thDist(dataset, features);
    }

    /**
     * Distance to 4th nearest neighbor. Used by DBSCAN to determine values of
     * its coefficients.
     *
     * @param dataset
     * @param features
     */
    private void avg4thDist(Dataset<E> dataset, HashMap<String, Double> features) {
        KnnFactory<E> kf = KnnFactory.getInstance();
        KNNSearch<E> alg = kf.getProvider("caching k-nn");
        if (alg == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        alg.setDataset(dataset);
        double sum = 0.0;
        for (int i = 0; i < dataset.size(); i++) {
            Neighbor[] nn = alg.knn(dataset.get(i), 4);
            sum += nn[3].distance;
        }
        features.put(AVG_FOURTH_NN, sum / dataset.size());
    }

}
