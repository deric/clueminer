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
package org.clueminer.graph.fast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.clueminer.graph.api.Edge;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class EdgeStoreTest {

    private EdgeStore edgeStore;

    @Before
    public void reset() {
        edgeStore = new EdgeStore();
    }

    @Test
    public void testDefaultSize() {
        int size = edgeStore.size();
        boolean isEmpty = edgeStore.isEmpty();

        assertEquals(isEmpty, true);
        assertEquals(size, 0);
    }

    @Test
    public void testSize() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(2);
        EdgeImpl e1 = edges[0];
        EdgeImpl e2 = edges[1];
        edgeStore.add(e1);
        edgeStore.add(e2);
        assertEquals(edgeStore.size(), 2);
        edgeStore.remove(e1);
        assertEquals(edgeStore.size(), 1);
        edgeStore.remove(e2);
        assertEquals(edgeStore.size(), 0);
        assertTrue(edgeStore.isEmpty());
    }

    @Test
    public void testAdd() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        boolean a = edgeStore.add(edge);
        boolean b = edgeStore.add(edge);

        Assert.assertEquals(a, true);
        Assert.assertEquals(b, false);

        Assert.assertEquals(edgeStore.isEmpty(), false);
        Assert.assertEquals(1, edgeStore.size());

        Assert.assertTrue(edgeStore.contains(edge));
        Assert.assertNotSame(edge.getStoreId(), EdgeStore.NULL_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        edgeStore.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOtherStore() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);

        EdgeStore edgeStore2 = new EdgeStore();
        edgeStore2.add(edge);
    }

    @Test
    public void testGet() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);

        Assert.assertEquals(edgeStore.get(0), edge);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInvalid() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);
        edgeStore.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegative() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);
        edgeStore.get(-1);
    }

    @Test
    public void testGetMultiBlock() {
        EdgeImpl[] edges = GraphGenerator.generateLargeEdgeList();

        edgeStore.addAll(Arrays.asList(edges));
        EdgeImpl firstEdge = edgeStore.get(0);
        EdgeImpl middleEdge = edgeStore.get(edges.length / 2);
        EdgeImpl lastEdge = edgeStore.get(edges.length - 1);

        Assert.assertEquals(firstEdge, edges[0]);
        Assert.assertEquals(middleEdge, edges[edges.length / 2]);
        Assert.assertEquals(lastEdge, edges[edges.length - 1]);
    }

    @Test
    public void testClear() {
        edgeStore.clear();

        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);
        edgeStore.clear();

        Assert.assertTrue(edgeStore.isEmpty());
        Assert.assertEquals(0, edgeStore.size());
        Assert.assertFalse(edgeStore.contains(edge));

        Assert.assertEquals(edge.getStoreId(), EdgeStore.NULL_ID);
    }

    @Test
    public void testRemove() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();

        edgeStore.add(edge);
        boolean a = edgeStore.remove(edge);
        boolean b = edgeStore.remove(edge);

        Assert.assertEquals(a, true);
        Assert.assertEquals(b, false);

        Assert.assertEquals(edgeStore.isEmpty(), true);
        Assert.assertEquals(0, edgeStore.size());

        Assert.assertFalse(edgeStore.contains(edge));
        Assert.assertSame(edge.getStoreId(), EdgeStore.NULL_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() {
        edgeStore.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOtherStore() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();

        edgeStore.add(edge);

        EdgeStore edgeStore2 = new EdgeStore();
        EdgeImpl edge2 = GraphGenerator.generateSingleEdge();

        edgeStore2.add(edge2);

        edgeStore.remove(edge2);
    }

    @Test
    public void testContains() {
        EdgeImpl edge = GraphGenerator.generateSingleEdge();
        edgeStore.add(edge);
        Assert.assertTrue(edgeStore.contains(edge));

        Assert.assertFalse(edgeStore.contains(GraphGenerator.generateSingleEdge()));
    }

    @Test
    public void testAddAll() {
        EdgeImpl[] edges = GraphGenerator.generateLargeEdgeList();

        boolean a = edgeStore.addAll(Arrays.asList(edges));

        Assert.assertEquals(edgeStore.size(), edges.length);
        Assert.assertTrue(a);
        testContainsOnly(edgeStore, Arrays.asList(edges));

        boolean b = edgeStore.addAll(Arrays.asList(edges));
        Assert.assertFalse(b);

        boolean c = edgeStore.addAll(new ArrayList<Edge>());
        Assert.assertFalse(c);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAllSelf() {
        edgeStore.addAll(edgeStore);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNull() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edges[1] = null;
        edgeStore.addAll(Arrays.asList(edges));
    }

    private void testContainsOnly(EdgeStore store, List<EdgeImpl> list) {
        for (EdgeImpl n : list) {
            Assert.assertTrue(store.contains(n));
            Assert.assertFalse(n.getStoreId() == EdgeStore.NULL_ID);
        }
        Assert.assertEquals(store.size(), list.size());

        Set<Edge> set = new HashSet<Edge>(list);
        for (Edge n : store) {
            Assert.assertTrue(set.remove(n));
        }
        Assert.assertTrue(set.isEmpty());
    }

}
