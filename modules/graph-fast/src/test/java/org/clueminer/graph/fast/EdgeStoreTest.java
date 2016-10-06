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

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
        edgeStore = new EdgeStore(false);
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

        EdgeStore edgeStore2 = new EdgeStore(false);
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
        //EdgeImpl[] edges = GraphGenerator.generateSmallEdgeList();

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

    @Test
    public void testRemoveAll() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));

        boolean a = edgeStore.removeAll(new ArrayList<Edge>());
        Assert.assertFalse(a);

        boolean b = edgeStore.removeAll(Arrays.asList(edges));
        Assert.assertTrue(b);
        Assert.assertTrue(edgeStore.isEmpty());

        testContainsNone(edgeStore, Arrays.asList(edges));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNull() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));
        edges[0] = null;
        edgeStore.removeAll(Arrays.asList(edges));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAllSelf() {
        edgeStore.removeAll(edgeStore);
    }

    @Test
    public void testRetainAll() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));

        EdgeImpl[] r = new EdgeImpl[]{edges[0]};
        boolean a = edgeStore.retainAll(Arrays.asList(r));
        boolean b = edgeStore.retainAll(Arrays.asList(r));

        Assert.assertTrue(a);
        Assert.assertFalse(b);

        Assert.assertEquals(1, edgeStore.size());
        Assert.assertTrue(edgeStore.contains(edges[0]));

        edgeStore.retainAll(new ArrayList());
        Assert.assertTrue(edgeStore.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNull() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));
        edges[0] = null;
        edgeStore.retainAll(Arrays.asList(edges));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetainAllSelf() {
        edgeStore.retainAll(edgeStore);
    }

    @Test
    public void testContainsAll() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));
        Assert.assertTrue(edgeStore.containsAll(Arrays.asList(edges)));
    }

    @Test
    public void testIterator() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));

        EdgeStore.EdgeStoreIterator itr = edgeStore.iterator();
        int index = 0;
        while (itr.hasNext()) {
            EdgeImpl n = itr.next();
            Assert.assertSame(n, edges[index++]);
        }
        Assert.assertEquals(index, edges.length);
    }

    @Test
    public void testIteratorRemove() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));

        EdgeStore.EdgeStoreIterator itr = edgeStore.iterator();
        int index = 0;
        while (itr.hasNext()) {
            EdgeImpl n = itr.next();
            itr.remove();
            Assert.assertEquals(edgeStore.size(), edges.length - ++index);
        }
        Assert.assertEquals(index, edges.length);
        testContainsNone(edgeStore, Arrays.asList(edges));
    }

    @Test
    public void testIteratorEmpty() {
        EdgeStore.EdgeStoreIterator itr = edgeStore.iterator();
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testIteratorAfterRemove() {
        EdgeStore edgeStore = new EdgeStore();
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        edgeStore.addAll(Arrays.asList(edges));
        edgeStore.remove(edges[1]);
        EdgeStore.EdgeStoreIterator itr = edgeStore.iterator();

        int index = 0;
        while (itr.hasNext()) {
            EdgeImpl n = itr.next();
            Assert.assertTrue(edgeStore.contains(n));
            index++;
        }
        Assert.assertEquals(index, edges.length - 1);
    }

    //@Test
    public void testEqualsAndHashCode() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        EdgeImpl[] edges2 = GraphGenerator.generateEdgeList(3);
        EdgeImpl[] edges3 = GraphGenerator.generateEdgeList(3);

        EdgeImpl t = edges3[0];
        edges3[0] = edges3[1];
        edges3[1] = t;

        EdgeStore edgeStore1 = new EdgeStore();
        EdgeStore edgeStore2 = new EdgeStore();

        Assert.assertEquals(edgeStore1, edgeStore2);
        Assert.assertEquals(edgeStore1.hashCode(), edgeStore2.hashCode());

        edgeStore1.addAll(Arrays.asList(edges));
        edgeStore2.addAll(Arrays.asList(edges2));

        Assert.assertEquals(edgeStore1, edgeStore2);
        Assert.assertEquals(edgeStore1.hashCode(), edgeStore2.hashCode());

        EdgeStore edgeStore3 = new EdgeStore();
        edgeStore3.addAll(Arrays.asList(edges3));

        Assert.assertNotEquals(edgeStore1, edgeStore3);
        Assert.assertNotEquals(edgeStore1.hashCode(), edgeStore3.hashCode());
    }

    @Test
    public void testToArray() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);
        Assert.assertArrayEquals(new EdgeImpl[0], edgeStore.toArray());
        edgeStore.addAll(Arrays.asList(edges));
        Assert.assertArrayEquals(edges, edgeStore.toArray());
        Assert.assertArrayEquals(edges, edgeStore.toArray(new Edge[0]));

        edgeStore.clear();
        Assert.assertArrayEquals(new EdgeImpl[0], edgeStore.toArray());
    }

    @Test
    public void testToArrayAfterRemove() {
        EdgeImpl[] edges = GraphGenerator.generateEdgeList(3);

        edgeStore.addAll(Arrays.asList(edges));
        edgeStore.remove(edges[0]);

        Assert.assertArrayEquals(edgeStore.toArray(), new EdgeImpl[]{edges[1], edges[2]});
    }

    @Test
    public void testRemoveAdd() {
        EdgeImpl[] edges = GraphGenerator.generateSmallEdgeList();
        edgeStore.addAll(Arrays.asList(edges));

        removeAndReAddSameEdges(edgeStore);

        Assert.assertEquals(edgeStore.size(), edges.length);
        Assert.assertArrayEquals(edgeStore.toArray(), edges);
    }

    @Test
    public void testGarbageSize() {
        EdgeImpl[] edges = GraphGenerator.generateLargeEdgeList();
        edgeStore.addAll(Arrays.asList(edges));

        Assert.assertEquals(edgeStore.garbageSize, 0);

        removeSomeEdges(edgeStore, 0.5f);
        Assert.assertEquals(edgeStore.garbageSize, (int) (edges.length * 0.5f));

        edgeStore.clear();
        Assert.assertEquals(0, edgeStore.garbageSize);
    }

    private void testContainsOnly(EdgeStore store, List<EdgeImpl> list) {
        for (EdgeImpl n : list) {
            Assert.assertTrue(store.contains(n));
            Assert.assertFalse(n.getStoreId() == EdgeStore.NULL_ID);
        }
        Assert.assertEquals(store.size(), list.size());

        HashSet<EdgeImpl> set = new HashSet<>(list);
        boolean ret;
        int i = 0;
        for (Edge n : store) {
            ret = set.remove(n);
            assertTrue("failed to remove " + i + "th edge", ret);
            i++;
        }
        Assert.assertTrue(set.isEmpty());
    }

    private void testContainsNone(EdgeStore store, List<EdgeImpl> list) {
        for (EdgeImpl n : list) {
            Assert.assertFalse(store.contains(n));
        }
    }

    private void removeAndReAddSameEdges(EdgeStore store) {
        List<EdgeImpl> edges = removeSomeEdges(store);
        Collections.reverse(edges);
        for (Edge edge : edges) {
            store.add(edge);
        }
    }

    private List<EdgeImpl> removeSomeEdges(EdgeStore store) {
        return removeSomeEdges(store, 0.3f);
    }

    private List<EdgeImpl> removeSomeEdges(EdgeStore store, float ratio) {
        int size = store.size;
        int s = (int) (size * ratio);
        int[] randomIndexes = generateRandomUniqueInts(s, size);
        List<EdgeImpl> edges = new ArrayList<>(s);
        for (int index : randomIndexes) {
            EdgeImpl edge = store.get(index);
            if (store.remove(edge)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    private int[] generateRandomUniqueInts(int count, int bound) {
        Random rand = new Random(123);
        IntSet set = new IntOpenHashSet();
        while (set.size() < count) {
            int number = rand.nextInt(bound);
            if (!set.contains(number)) {
                set.add(number);
            }
        }
        return set.toIntArray();
    }

}
