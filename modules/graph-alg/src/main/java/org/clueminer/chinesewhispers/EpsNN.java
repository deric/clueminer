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
package org.clueminer.chinesewhispers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.clueminer.clustering.algorithm.DBSCAN;
import org.clueminer.clustering.algorithm.DBSCANParamEstim;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.neighbor.RnnFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Connect data points when points are located within EPS distance from each other.
 *
 * @author deric
 * @param <E> base data row type
 */
@ServiceProvider(service = GraphConvertor.class)
public class EpsNN<E extends Instance> implements GraphConvertor<E> {

    private Distance dm;
    private static final String NAME = "eps NN";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Convert input data to a graph
     *
     * @param graph
     * @param dataset
     */
    @Override
    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params) {
        // the semantic is same as in case of DBSCAN
        double eps;
        if (!params.containsKey(DBSCAN.EPS)) {
            DBSCANParamEstim<E> estimation = DBSCANParamEstim.getInstance();
            estimation.estimate(dataset, params);
        }
        eps = params.getDouble(DBSCAN.EPS);

        RnnFactory rnnf = RnnFactory.getInstance();
        RNNSearch<E> rnn = rnnf.getProvider(params.get("RNN", "KD-tree"));
        rnn.setDataset(dataset);
        rnn.setDistanceMeasure(dm);

        Node<E> source, target;
        Edge edge;
        List<Neighbor<E>> nn = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            source = graph.getNode(mapping[i]);
            E curr = dataset.get(i);

            nn.clear();
            rnn.range(curr, eps, nn);
            for (Neighbor neighbor : nn) {
                target = graph.getNode(mapping[neighbor.index]);
                edge = graph.getFactory().newEdge(source, target);
                graph.addEdge(edge);
            }
        }
    }

    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params, HashSet<Integer> noise) {
        // the semantic is same as in case of DBSCAN
        double eps;
        if (!params.containsKey(DBSCAN.EPS)) {
            DBSCANParamEstim<E> estimation = DBSCANParamEstim.getInstance();
            estimation.estimate(dataset, params);
        }
        eps = params.getDouble(DBSCAN.EPS);

        RnnFactory rnnf = RnnFactory.getInstance();
        RNNSearch<E> rnn = rnnf.getProvider(params.get("RNN", "KD-tree"));
        rnn.setDataset(dataset);
        rnn.setDistanceMeasure(dm);
        rnn.setExclude(noise);

        Node<E> source, target;
        Edge edge;
        List<Neighbor<E>> nn = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            source = graph.getNode(mapping[i]);
            E curr = dataset.get(i);

            nn.clear();
            rnn.range(curr, eps, nn);
            for (Neighbor neighbor : nn) {
                target = graph.getNode(mapping[neighbor.index]);
                edge = graph.getFactory().newEdge(source, target);
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

    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params) {
        GraphBuilder gb = graph.getFactory();
        Long[] mapping = gb.createNodesFromInput(dataset, graph);
        createEdges(graph, dataset, mapping, params);
    }

    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params, List<E> noise) {
        GraphBuilder gb = graph.getFactory();

        int size = noise != null ? noise.size() : 50;
        HashSet<Integer> hash = new HashSet<>(size);
        for (E inst : noise) {
            hash.add(inst.getIndex());
        }

        Long[] mapping = gb.createNodesFromInput(dataset, graph, hash);
        createEdges(graph, dataset, mapping, params, hash);
    }
}
