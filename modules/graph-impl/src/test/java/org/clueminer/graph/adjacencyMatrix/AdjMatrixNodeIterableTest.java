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
package org.clueminer.graph.adjacencyMatrix;

import java.util.Collection;
import java.util.Iterator;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AdjMatrixNodeIterableTest {

    private final AdjMatrixNodeIterable subject;

    public AdjMatrixNodeIterableTest() {
        Node[] nodes = new AdjMatrixNode[3];
        nodes[0] = new AdjMatrixNode(0, "a");
        nodes[1] = new AdjMatrixNode(1, "b");
        nodes[2] = new AdjMatrixNode(2, "c");
        subject = new AdjMatrixNodeIterable(nodes, nodes.length);
    }

    @Test
    public void testIterator() {
        Iterator<Node> it = subject.iterator();
        assertEquals(true, it.hasNext());
        int i = 0;
        Node node;
        while (it.hasNext()) {
            node = it.next();
            assertNotNull(node);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testEmptyIterator() {
        Node[] nodes = new AdjMatrixNode[0];
        AdjMatrixNodeIterable test = new AdjMatrixNodeIterable(nodes, nodes.length);
        Iterator<Node> it = test.iterator();
        assertEquals(false, it.hasNext());
    }

    @Test
    public void testToArray() {
        Node[] nodes = subject.toArray();
        assertEquals(3, nodes.length);
    }

    @Test
    public void testToCollection() {
        Collection<Node> col = subject.toCollection();
        assertEquals(3, col.size());
    }

    /**
     * Iterator is given bigger array than is its actual non-empty length
     */
    @Test
    public void testEmptyArray() {
        Node[] nodes = new AdjMatrixNode[5];
        nodes[0] = new AdjMatrixNode(0, "a");
        AdjMatrixNodeIterable test = new AdjMatrixNodeIterable(nodes, 1);
        for (Node n : test) {
            assertNotNull(n);
        }
    }

}
