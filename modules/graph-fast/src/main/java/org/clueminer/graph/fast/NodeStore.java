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

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author deric
 */
public class NodeStore implements Collection<Node>, NodeIterable {

    protected final static int NULL_ID = -1;
    protected int size;
    protected int garbageSize;
    protected int blocksCount;
    protected int currentBlockIndex;
    protected NodeBlock blocks[];
    protected NodeBlock currentBlock;
    protected Long2IntOpenHashMap dictionary;
    protected final GraphVersion version;
    private boolean allowReferences = false;

    public NodeStore() {
        initStore();
        this.version = null;
    }

    public NodeStore(boolean allowRef) {
        initStore();
        this.version = null;
        this.allowReferences = allowRef;
    }

    private void initStore() {
        this.size = 0;
        this.garbageSize = 0;
        this.blocksCount = 1;
        this.currentBlockIndex = 0;
        this.blocks = new NodeBlock[FastGraphConfig.NODESTORE_DEFAULT_BLOCKS];
        this.blocks[0] = new NodeBlock(0);
        this.currentBlock = blocks[currentBlockIndex];
        this.dictionary = new Long2IntOpenHashMap(FastGraphConfig.NODESTORE_DEFAULT_DICTIONARY_SIZE, FastGraphConfig.NODESTORE_DICTIONARY_LOAD_FACTOR);
        this.dictionary.defaultReturnValue(NULL_ID);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    public NodeImpl get(final int id) {
        checkValidId(id);

        return blocks[id / FastGraphConfig.NODESTORE_BLOCK_SIZE].get(id);
    }

    public NodeImpl get(final Object id) {
        int index = dictionary.getOrDefault(id, NodeStore.NULL_ID);
        if (index != NodeStore.NULL_ID) {
            return get(index);
        }
        return null;
    }

    private void ensureCapacity(final int capacity) {
        assert capacity > 0;

        int blockCapacity = currentBlock.getCapacity();
        while (capacity > blockCapacity) {
            if (currentBlockIndex == blocksCount - 1) {
                int blocksNeeded = (int) Math.ceil((capacity - blockCapacity) / (double) FastGraphConfig.NODESTORE_BLOCK_SIZE);
                for (int i = 0; i < blocksNeeded; i++) {
                    if (blocksCount == blocks.length) {
                        NodeBlock[] newBlocks = new NodeBlock[blocksCount + 1];
                        System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
                        blocks = newBlocks;
                    }
                    NodeBlock block = blocks[blocksCount];
                    if (block == null) {
                        block = new NodeBlock(blocksCount);
                        blocks[blocksCount] = block;
                    }
                    if (blockCapacity == 0 && i == 0) {
                        currentBlockIndex = blocksCount;
                        currentBlock = block;
                    }
                    blocksCount++;
                }
                break;
            } else {
                currentBlockIndex++;
                currentBlock = blocks[currentBlockIndex];
                blockCapacity = currentBlock.getCapacity();
            }
        }
    }

    @Override
    public boolean contains(Object o) {
        checkNonNullNodeObject(o);

        NodeImpl node = (NodeImpl) o;
        int id = node.getStoreId();
        if (id != NodeStore.NULL_ID) {
            if (get(id) == node) {
                return true;
            }
        }

        return false;
    }

    @Override
    public NodeStoreIterator iterator() {
        return new NodeStoreIterator();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        checkNonNullObject(array);

        if (array.length < size()) {
            array = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size());
        }
        if (garbageSize == 0) {
            for (int i = 0; i < blocksCount; i++) {
                NodeBlock block = blocks[i];
                System.arraycopy(block.backingArray, 0, array, block.offset, block.nodeLength);
            }
        } else {
            Iterator<Node> itr = iterator();
            int offset = 0;
            while (itr.hasNext()) {
                NodeImpl n = (NodeImpl) itr.next();
                array[offset++] = (T) n;
            }
        }

        return array;
    }

    @Override
    public boolean add(Node n) {
        checkNonNullNodeObject(n);

        NodeImpl node = (NodeImpl) n;
        if (allowReferences || node.storeId == NodeStore.NULL_ID) {
            checkIdDoesntExist(n.getId());

            incrementVersion();

            if (garbageSize > 0) {
                for (int i = 0; i < blocksCount; i++) {
                    NodeBlock nodeBlock = blocks[i];
                    if (nodeBlock.hasGarbage()) {
                        nodeBlock.set(node);
                        garbageSize--;
                        dictionary.put(node.getId(), node.storeId);
                        break;
                    }
                }
            } else {
                ensureCapacity(1);
                currentBlock.add(node);
                dictionary.put(node.getId(), node.storeId);
            }

            size++;

            return true;
        } else if (isValidIndex(node.storeId) && get(node.storeId) == node) {
            return false;
        } else {
            throw new IllegalArgumentException("The node already belongs to another store");
        }
    }

    @Override
    public boolean remove(Object o) {
        checkNonNullNodeObject(o);

        NodeImpl node = (NodeImpl) o;
        int id = node.storeId;
        if (id != NodeStore.NULL_ID) {
            checkNodeExists(node);

            node.clearAttributes();

            incrementVersion();

            int storeIndex = id / FastGraphConfig.NODESTORE_BLOCK_SIZE;
            NodeBlock block = blocks[storeIndex];
            block.remove(node);
            size--;
            garbageSize++;
            dictionary.remove(node.getId());
            trimDictionary();

            for (int i = storeIndex; i == (blocksCount - 1) && block.garbageLength == block.nodeLength && i >= 0;) {
                if (i != 0) {
                    blocks[i] = null;
                    blocksCount--;
                    garbageSize -= block.nodeLength;
                    block = blocks[--i];
                    currentBlock = block;
                    currentBlockIndex--;
                } else {
                    currentBlock.clear();
                    garbageSize = 0;
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        checkCollection(c);

        if (!c.isEmpty()) {
            int found = 0;
            for (Object o : c) {
                if (contains((NodeImpl) o)) {
                    found++;
                }
            }
            return found == c.size();
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Node> c) {
        checkCollection(c);

        if (!c.isEmpty()) {
            int capacityNeeded = c.size() - garbageSize;
            if (capacityNeeded > 0) {
                ensureCapacity(capacityNeeded);
            }
            boolean changed = false;
            Iterator<? extends Node> itr = c.iterator();
            while (itr.hasNext()) {
                Node e = itr.next();
                if (add(e)) {
                    changed = true;
                }
            }
            return changed;
        }
        return false;
    }

    private void trimDictionary() {
        dictionary.trim(Math.max(FastGraphConfig.NODESTORE_BLOCK_SIZE, size * 2));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkCollection(c);

        if (!c.isEmpty()) {
            boolean changed = false;
            Iterator itr = c.iterator();
            while (itr.hasNext()) {
                Object o = itr.next();
                if (remove(o)) {
                    changed = true;
                }
            }
            return changed;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkCollection(c);

        if (!c.isEmpty()) {
            ObjectSet<NodeImpl> set = new ObjectOpenHashSet(c.size());
            for (Object o : c) {
                checkNonNullObject(o);
                checkNodeExists((NodeImpl) o);
                set.add((NodeImpl) o);
            }

            boolean changed = false;
            Iterator<Node> itr = iterator();
            while (itr.hasNext()) {
                Node e = itr.next();
                if (!set.contains(e)) {
                    itr.remove();
                    changed = true;
                }
            }
            return changed;
        } else {
            clear();
        }
        return false;
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            incrementVersion();
        }

        for (NodeStoreIterator itr = new NodeStoreIterator(); itr.hasNext();) {
            NodeImpl node = itr.next();
            node.setStoreId(NodeStore.NULL_ID);
        }
        initStore();
    }

    @Override
    public Node[] toArray() {
        NodeImpl[] array = new NodeImpl[size];
        if (garbageSize == 0) {
            for (int i = 0; i < blocksCount; i++) {
                NodeBlock block = blocks[i];
                System.arraycopy(block.backingArray, 0, array, block.offset, block.nodeLength);
            }
        } else {
            Iterator<Node> itr = iterator();
            int offset = 0;
            while (itr.hasNext()) {
                NodeImpl n = (NodeImpl) itr.next();
                array[offset++] = n;
            }
        }

        return array;
    }

    @Override
    public Collection<Node> toCollection() {
        List<Node> list = new ArrayList<>(size);

        NodeStoreIterator itr = (NodeStoreIterator) iterator();
        while (itr.hasNext()) {
            NodeImpl n = itr.next();
            list.add(n);
        }

        return list;
    }

    @Override
    public void doBreak() {
        //
    }

    protected final class NodeStoreIterator implements Iterator<Node> {

        protected int cursor;
        protected NodeImpl pointer;
        protected int blockIndex;
        protected NodeImpl[] backingArray;
        protected int blockLength;

        public NodeStoreIterator() {
            this.backingArray = blocks[blockIndex].backingArray;
            this.blockLength = blocks[blockIndex].nodeLength;
        }

        @Override
        public boolean hasNext() {
            pointer = null;
            while (cursor == blockLength || ((pointer = backingArray[cursor++]) == null)) {
                if (cursor == blockLength) {
                    if (++blockIndex < blocksCount) {
                        backingArray = blocks[blockIndex].backingArray;
                        blockLength = blocks[blockIndex].nodeLength;
                        cursor = 0;
                    } else {
                        break;
                    }
                }
            }
            return pointer != null;
        }

        @Override
        public NodeImpl next() {
            return pointer;
        }

        @Override
        public void remove() {
            NodeStore.this.remove(pointer);
        }
    }

    private void checkNonNullObject(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    void checkNonNullNodeObject(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof NodeImpl)) {
            throw new ClassCastException("Object must be a NodeImpl object");
        }
    }

    void checkNodeExists(final NodeImpl node) {
        if (get(node.storeId) != node) {
            throw new IllegalArgumentException("The node is invalid");
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 5 * hash + this.size;
        Iterator<Node> itr = this.iterator();
        while (itr.hasNext()) {
            hash += 67 * itr.next().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeStore other = (NodeStore) obj;
        if (this.size != other.size) {
            return false;
        }
        Iterator<Node> itr1 = this.iterator();
        Iterator<Node> itr2 = other.iterator();
        while (itr1.hasNext()) {
            if (!itr2.hasNext()) {
                return false;
            }
            if (!itr1.next().equals(itr2.next())) {
                return false;
            }
        }
        return true;
    }

    private void checkCollection(final Collection<?> collection) {
        if (collection == this) {
            throw new IllegalArgumentException("Can't pass itself");
        }
    }

    protected static class NodeBlock {

        protected final int offset;
        protected final short[] garbageArray;
        protected final NodeImpl[] backingArray;
        protected int nodeLength;
        protected int garbageLength;

        public NodeBlock(int index) {
            this.offset = index * FastGraphConfig.NODESTORE_BLOCK_SIZE;
            if (FastGraphConfig.NODESTORE_BLOCK_SIZE >= Short.MAX_VALUE - Short.MIN_VALUE) {
                throw new RuntimeException("BLOCK SIZE can't exceed 65535");
            }
            this.garbageArray = new short[FastGraphConfig.NODESTORE_BLOCK_SIZE];
            this.backingArray = new NodeImpl[FastGraphConfig.NODESTORE_BLOCK_SIZE];
        }

        public boolean hasGarbage() {
            return garbageLength > 0;
        }

        public int getCapacity() {
            return FastGraphConfig.NODESTORE_BLOCK_SIZE - nodeLength - garbageLength;
        }

        public void add(NodeImpl k) {
            int i = nodeLength++;
            backingArray[i] = k;
            k.setStoreId(i + offset);
        }

        public void set(NodeImpl k) {
            int i = garbageArray[--garbageLength] - Short.MIN_VALUE;
            backingArray[i] = k;
            k.setStoreId(i + offset);
        }

        public NodeImpl get(int id) {
            return backingArray[id - offset];
        }

        public void remove(NodeImpl k) {
            int i = k.getStoreId() - offset;
            backingArray[i] = null;
            garbageArray[garbageLength++] = (short) (i + Short.MIN_VALUE);
            k.setStoreId(NULL_ID);
        }

        public void clear() {
            nodeLength = 0;
            garbageLength = 0;
        }
    }

    private int incrementVersion() {
        if (version != null) {
            return version.incrementAndGetNodeVersion();
        }
        return 0;
    }

    private void checkValidId(final int id) {
        if (id < 0 || !isValidIndex(id)) {
            throw new IllegalArgumentException("Node id=" + id + " is invalid");
        }
    }

    private boolean isValidIndex(int id) {
        return !(id < 0 || id >= currentBlock.offset + currentBlock.nodeLength);
    }

    private void checkIdDoesntExist(Object id) {
        if (dictionary.containsKey(id)) {
            throw new IllegalArgumentException("The node id already exist");
        }
    }

}
