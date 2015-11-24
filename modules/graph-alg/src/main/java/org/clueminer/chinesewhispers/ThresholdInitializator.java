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

import static org.clueminer.chinesewhispers.ChineseWhispers.EDGE_THRESHOLD;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = GraphConvertor.class)
public class ThresholdInitializator<E extends Instance> implements GraphConvertor<E> {

    private Distance dm;
    private static final String name = "distance threshold";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Convert input data to a graph
     *
     * @param graph
     * @param dataset
     */
    @Override
    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params) {
        double dist;
        double edgeThreshold = params.getDouble(EDGE_THRESHOLD, 1.0);
        Node<E> source, target;
        Edge edge;
        for (int i = 0; i < dataset.size(); i++) {
            source = graph.getNode(mapping[i]);
            E curr = dataset.get(i);
            for (int j = 0; j < i; j++) {
                if (i != j) {
                    dist = dm.measure(curr, dataset.get(j));
                    if (dm.compare(dist, edgeThreshold)) {
                        target = graph.getNode(mapping[j]);
                        edge = graph.getFactory().newEdge(source, target);
                        graph.addEdge(edge);
                    }
                }
            }
        }
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }
}
