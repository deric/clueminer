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

import org.clueminer.graph.api.Edge;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListFactoryTest {

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
}
