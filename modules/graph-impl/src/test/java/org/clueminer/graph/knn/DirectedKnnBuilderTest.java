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
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class DirectedKnnBuilderTest<E extends Instance> {

    private final DirectedKnnBuilder subject;

    public DirectedKnnBuilderTest() {
        subject = new DirectedKnnBuilder<>();
    }

    @Test
    public void testCreateEdges() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.irisDataset();
        Props params = new Props();
        Graph graph = new AdjListGraph();
        Long[] mapping = AdjListFactory.getInstance().createNodesFromInput(dataset, graph);
        subject.createEdges(graph, dataset, mapping, params);
        assertEquals(dataset.size(), graph.getNodeCount());
        //std: 510
        assertEquals(510, graph.getEdgeCount());
    }

}
