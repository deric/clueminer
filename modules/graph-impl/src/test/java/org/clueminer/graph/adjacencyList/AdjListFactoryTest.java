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
package org.clueminer.graph.adjacencyList;

import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Hamster
 * @param <E>
 */
public class AdjListFactoryTest<E extends Instance> {

    @Test
    public void singletonTest() {
        AdjListFactory first = AdjListFactory.getInstance();
        AdjListFactory second = AdjListFactory.getInstance();
        assertSame(first, second);
    }

    /**
     * The order of tests is not guaranteed, thus we have to fetch current node
     * count first
     */
    @Test
    public void newNodeTest() {
        long id;
        long expected = AdjListFactory.getNodeCount();
        AdjListFactory factory = AdjListFactory.getInstance();
        id = factory.newNode().getId();
        assertEquals(expected++, id);
        id = factory.newNode().getId();
        assertEquals(expected++, id);
        id = factory.newNode().getId();
        assertEquals(expected++, id);
        id = factory.newNode().getId();
        assertEquals(expected++, id);
        id = factory.newNode().getId();
        assertEquals(expected++, id);
    }

    @Test
    public void newEdgeTest() {
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        long expected = AdjListFactory.getEdgeCount();
        Edge edge = AdjListFactory.getInstance().newEdge(source, target);
        assertEquals(expected++, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        source = new AdjListNode(3);
        target = new AdjListNode(4);
        edge = AdjListFactory.getInstance().newEdge(source, target, true);
        assertEquals(expected++, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        assertTrue(edge.isDirected());
    }

    @Test
    public void testNoise() {
        AdjListFactory subject = AdjListFactory.getInstance();
        Dataset<E> data = (Dataset<E>) FakeDatasets.irisDataset();
        Graph<E> graph = new AdjListGraph();
        HashSet<Integer> noise = new HashSet<>();
        noise.add(0);
        noise.add(1);
        noise.add(9);
        noise.add(42);

        Long[] mapping = subject.createNodesFromInput(data, graph, noise);
        assertEquals(data.size(), mapping.length);
        //should not be mapped to anything
        assertEquals(null, mapping[0]);

    }
}
