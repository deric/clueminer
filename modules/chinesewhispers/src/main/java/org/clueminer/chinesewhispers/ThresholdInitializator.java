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
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = GraphConvertor.class)
public class ThresholdInitializator implements GraphConvertor {

    private DistanceMeasure dm;
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
    public void createEdges(Graph graph, Dataset<? extends Instance> dataset, Long[] mapping, Props params) {
        double dist;
        double edgeThreshold = params.getDouble(EDGE_THRESHOLD, 1.0);
        for (int i = 0; i < dataset.size(); i++) {
            Node source = graph.getNode(mapping[i]);
            Instance curr = dataset.get(i);
            for (int j = 0; j < i; j++) {
                if (i != j) {
                    dist = dm.measure(curr, dataset.get(j));
                    if (dm.compare(dist, edgeThreshold)) {
                        //if (dist < edgeThreshold) {
                        Node target = graph.getNode(mapping[j]);
                        Edge edge = graph.getFactory().newEdge(source, target);
                        graph.addEdge(edge);
                    }
                }
            }
        }
    }

    @Override
    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }
}
