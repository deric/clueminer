/*
 * Copyright (C) 2011-2017 clueminer.org
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
import java.util.Set;
import org.clueminer.graph.api.Node;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NodeStoreTest {

    private NodeStore nodeStore;

    @Before
    public void reset() {
        nodeStore = new NodeStore(false);
    }

    @Test
    public void testDefaultSize() {
        int size = nodeStore.size();
        boolean isEmpty = nodeStore.isEmpty();

        assertEquals(isEmpty, true);
        assertEquals(size, 0);
    }

    @Test
    public void testSize() {
        NodeImpl n1 = new NodeImpl(0l);
        NodeImpl n2 = new NodeImpl(1l);
        nodeStore.add(n1);
        nodeStore.add(n2);
        assertEquals(nodeStore.size(), 2);
        nodeStore.remove(n1);
        assertEquals(nodeStore.size(), 1);
        nodeStore.remove(n2);
        assertEquals(nodeStore.size(), 0);
        assertTrue(nodeStore.isEmpty());
    }

    @Test
    public void testAdd() {
        NodeImpl node = new NodeImpl(0l);
        boolean a = nodeStore.add(node);
        boolean b = nodeStore.add(node);

        assertEquals(a, true);
        assertEquals(b, false);

        assertEquals(nodeStore.isEmpty(), false);
        assertEquals(nodeStore.size(), 1);

        assertTrue(nodeStore.contains(node));
        assertNotSame(node.getStoreId(), NodeStore.NULL_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        nodeStore.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOtherStore() {
        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);

        NodeStore nodeStore2 = new NodeStore(false);
        nodeStore2.add(node);
    }

    @Test
    public void testGet() {
        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);

        assertEquals(nodeStore.get(0), node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInvalid() {
        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);
        nodeStore.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegative() {
        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);
        nodeStore.get(-1);
    }

    @Test
    public void testGetMultiBlock() {
        NodeImpl[] nodes = GraphGenerator.generateLargeNodeList();

        nodeStore.addAll(Arrays.asList(nodes));
        NodeImpl firstNode = nodeStore.get(0);
        NodeImpl middleNode = nodeStore.get(nodes.length / 2);
        NodeImpl lastNode = nodeStore.get(nodes.length - 1);

        assertEquals(firstNode, nodes[0]);
        assertEquals(middleNode, nodes[nodes.length / 2]);
        assertEquals(lastNode, nodes[nodes.length - 1]);
    }

    @Test
    public void testClear() {
        nodeStore.clear();

        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);
        nodeStore.clear();

        assertTrue(nodeStore.isEmpty());
        assertEquals(nodeStore.size(), 0);
        assertFalse(nodeStore.contains(node));

        assertEquals(node.getStoreId(), NodeStore.NULL_ID);
    }

    @Test
    public void testRemove() {
        NodeImpl node = new NodeImpl(0l);

        nodeStore.add(node);
        boolean a = nodeStore.remove(node);
        boolean b = nodeStore.remove(node);

        assertEquals(a, true);
        assertEquals(b, false);

        assertEquals(nodeStore.isEmpty(), true);
        assertEquals(nodeStore.size(), 0);

        assertFalse(nodeStore.contains(node));
        assertSame(node.getStoreId(), NodeStore.NULL_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() {
        nodeStore.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOtherStore() {
        NodeImpl node = new NodeImpl(0l);

        nodeStore.add(node);

        NodeStore nodeStore2 = new NodeStore();
        NodeImpl node2 = new NodeImpl(0l);

        nodeStore2.add(node2);

        nodeStore.remove(node2);
    }

    @Test
    public void testContains() {
        NodeImpl node = new NodeImpl(0l);
        nodeStore.add(node);
        assertTrue(nodeStore.contains(node));

        assertFalse(nodeStore.contains(new NodeImpl(0l)));
        assertFalse(nodeStore.contains(new NodeImpl(2l)));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNull() {
        NodeStore nodeStore = new NodeStore();
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l)};
        nodeStore.addAll(Arrays.asList(nodes));
        nodes[0] = null;
        nodeStore.removeAll(Arrays.asList(nodes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAllSelf() {
        NodeStore nodeStore = new NodeStore();
        nodeStore.removeAll(nodeStore);
    }

    @Test
    public void testRetainAll() {
        NodeStore nodeStore = new NodeStore();
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        nodeStore.addAll(Arrays.asList(nodes));

        NodeImpl[] r = new NodeImpl[]{nodes[0]};
        boolean a = nodeStore.retainAll(Arrays.asList(r));
        boolean b = nodeStore.retainAll(Arrays.asList(r));

        Assert.assertTrue(a);
        Assert.assertFalse(b);

        Assert.assertEquals(nodeStore.size(), 1);
        Assert.assertTrue(nodeStore.contains(nodes[0]));

        nodeStore.retainAll(new ArrayList());
        Assert.assertTrue(nodeStore.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNull() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        nodeStore.addAll(Arrays.asList(nodes));
        nodes[0] = null;
        nodeStore.retainAll(Arrays.asList(nodes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetainAllSelf() {
        nodeStore.retainAll(nodeStore);
    }

    @Test
    public void testContainsAll() {
        NodeStore nodeStore = new NodeStore();
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};

        nodeStore.addAll(Arrays.asList(nodes));
        Assert.assertTrue(nodeStore.containsAll(Arrays.asList(nodes)));
    }

    @Test
    public void testIterator() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        nodeStore.addAll(Arrays.asList(nodes));

        NodeStore.NodeStoreIterator itr = nodeStore.iterator();
        int index = 0;
        while (itr.hasNext()) {
            NodeImpl n = itr.next();
            Assert.assertSame(n, nodes[index++]);
        }
        Assert.assertEquals(index, nodes.length);
    }

    @Test
    public void testIteratorRemove() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        nodeStore.addAll(Arrays.asList(nodes));

        NodeStore.NodeStoreIterator itr = nodeStore.iterator();
        int index = 0;
        while (itr.hasNext()) {
            NodeImpl n = itr.next();
            itr.remove();
            Assert.assertEquals(nodeStore.size(), nodes.length - ++index);
        }
        Assert.assertEquals(index, nodes.length);
        testContainsNone(nodeStore, Arrays.asList(nodes));
    }

    @Test
    public void testIteratorEmpty() {
        NodeStore.NodeStoreIterator itr = nodeStore.iterator();
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testIteratorAfterRemove() {
        NodeStore nodeStore = new NodeStore();
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        nodeStore.addAll(Arrays.asList(nodes));
        nodeStore.remove(nodes[1]);
        NodeStore.NodeStoreIterator itr = nodeStore.iterator();

        int index = 0;
        while (itr.hasNext()) {
            NodeImpl n = itr.next();
            Assert.assertTrue(nodeStore.contains(n));
            index++;
        }
        Assert.assertEquals(index, nodes.length - 1);

    }

    @Test
    public void testEqualsAndHashCode() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        NodeImpl[] nodes2 = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        NodeImpl[] nodes3 = new NodeImpl[]{new NodeImpl(1l), new NodeImpl(0l), new NodeImpl(2l)};

        NodeStore nodeStore1 = new NodeStore();
        NodeStore nodeStore2 = new NodeStore();

        Assert.assertEquals(nodeStore1, nodeStore2);
        Assert.assertEquals(nodeStore1.hashCode(), nodeStore2.hashCode());

        nodeStore1.addAll(Arrays.asList(nodes));
        nodeStore2.addAll(Arrays.asList(nodes2));

        Assert.assertEquals(nodeStore1, nodeStore2);
        Assert.assertEquals(nodeStore1.hashCode(), nodeStore2.hashCode());

        NodeStore nodeStore3 = new NodeStore();
        nodeStore3.addAll(Arrays.asList(nodes3));

        Assert.assertNotEquals(nodeStore1, nodeStore3);
        Assert.assertNotEquals(nodeStore1.hashCode(), nodeStore3.hashCode());
    }

    @Test
    public void testToArray() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};
        Assert.assertArrayEquals(new NodeImpl[0], nodeStore.toArray());
        nodeStore.addAll(Arrays.asList(nodes));
        Assert.assertArrayEquals(nodes, nodeStore.toArray());
        Assert.assertArrayEquals(nodes, nodeStore.toArray(new Node[0]));

        nodeStore.clear();
        Assert.assertArrayEquals(new NodeImpl[0], nodeStore.toArray());
    }

    @Test
    public void testToArrayAfterRemove() {
        NodeImpl[] nodes = new NodeImpl[]{new NodeImpl(0l), new NodeImpl(1l), new NodeImpl(2l)};

        nodeStore.addAll(Arrays.asList(nodes));
        nodeStore.remove(nodes[0]);

        Assert.assertArrayEquals(nodeStore.toArray(), new NodeImpl[]{nodes[1], nodes[2]});
    }

    @Test
    public void testRemoveAdd() {
        NodeImpl[] nodes = GraphGenerator.generateSmallNodeList();
        nodeStore.addAll(Arrays.asList(nodes));

        removeAndReAddSameNodes(nodeStore);

        assertEquals(nodeStore.size(), nodes.length);
        Assert.assertArrayEquals(nodeStore.toArray(), nodes);
    }

    @Test
    public void testGarbageSize() {
        NodeImpl[] nodes = GraphGenerator.generateLargeNodeList();
        nodeStore.addAll(Arrays.asList(nodes));

        assertEquals(nodeStore.garbageSize, 0);

        removeSomeNodes(nodeStore, 0.5f);
        assertEquals(nodeStore.garbageSize, (int) (nodes.length * 0.5f));

        nodeStore.clear();
        assertEquals(nodeStore.garbageSize, 0);
    }

    @Test
    public void testBlockCounts() {
        NodeImpl[] nodes = GraphGenerator.generateLargeNodeList();
        nodeStore.addAll(Arrays.asList(nodes));
        int blockCount = nodeStore.blocksCount;

        for (int i = 0; i < FastGraphConfig.NODESTORE_BLOCK_SIZE; i++) {
            nodeStore.remove(nodes[nodes.length - 1 - i]);
        }

        assertEquals(nodeStore.blocksCount, blockCount - 1);
    }

    @Test
    public void testBlockCountsEmpty() {
        NodeImpl[] nodes = GraphGenerator.generateLargeNodeList();
        nodeStore.addAll(Arrays.asList(nodes));
        nodeStore.removeAll(Arrays.asList(nodes));

        assertEquals(nodeStore.blocksCount, 1);
        assertEquals(nodeStore.blocks[0], nodeStore.currentBlock);
        assertEquals(nodeStore.currentBlockIndex, 0);
        assertEquals(nodeStore.size, 0);
        assertEquals(nodeStore.garbageSize, 0);
    }

    @Test
    public void testDictionary() {
        NodeImpl node = new NodeImpl(1l);
        nodeStore.add(node);

        assertTrue(nodeStore.contains(node));
        assertEquals(nodeStore.get(1l), node);
        assertNull(nodeStore.get(0l));
        assertFalse(nodeStore.contains(new NodeImpl(0l)));

        nodeStore.remove(node);
        assertFalse(nodeStore.contains(node));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDictionaryDuplicate() {
        NodeImpl node = new NodeImpl(1l);
        nodeStore.add(node);
        NodeImpl node2 = new NodeImpl(1l);
        nodeStore.add(node2);
    }

    private void testContainsOnly(NodeStore store, List<NodeImpl> list) {
        for (NodeImpl n : list) {
            assertTrue(store.contains(n));
            assertFalse(n.getStoreId() == NodeStore.NULL_ID);
        }
        assertEquals(store.size(), list.size());

        Set<Node> set = new HashSet<Node>(list);
        for (Node n : store) {
            assertTrue(set.remove(n));
        }
        assertTrue(set.isEmpty());
    }

    private void testContainsNone(NodeStore store, List<NodeImpl> list) {
        for (NodeImpl n : list) {
            assertFalse(store.contains(n));
        }
    }

    private void removeAndReAddSameNodes(NodeStore store) {
        List<NodeImpl> nodes = removeSomeNodes(store);
        Collections.reverse(nodes);
        for (Node node : nodes) {
            store.add(node);
        }
    }

    private List<NodeImpl> removeSomeNodes(NodeStore store) {
        return removeSomeNodes(store, 0.3f);
    }

    private List<NodeImpl> removeSomeNodes(NodeStore store, float ratio) {
        int size = store.size;
        int s = (int) (size * ratio);
        int[] randomIndexes = generateRandomUniqueInts(s, size);
        List<NodeImpl> nodes = new ArrayList<>(s);
        for (int index : randomIndexes) {
            NodeImpl node = store.get(index);
            if (store.remove(node)) {
                nodes.add(node);
            }
        }
        return nodes;
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
