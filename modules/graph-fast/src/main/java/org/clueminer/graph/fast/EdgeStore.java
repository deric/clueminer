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

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class EdgeStore implements Collection<Edge>, EdgeIterable {

    //Const
    protected final static int NULL_ID = -1;
    protected final static int NODE_BITS = 31;
    //Data
    protected int size;
    protected int garbageSize;
    protected int blocksCount;
    protected int currentBlockIndex;
    protected EdgeBlock blocks[];
    protected EdgeBlock currentBlock;
    protected Long2IntOpenHashMap dictionary;
    //Stats
    protected int undirectedSize;
    protected int mutualEdgesSize;
    //Version
    protected final GraphVersion version;

    public EdgeStore() {
        initStore();
        this.version = null;
    }

    private void initStore() {
        this.size = 0;
        this.garbageSize = 0;
        this.blocksCount = 1;
        this.currentBlockIndex = 0;
        this.blocks = new EdgeBlock[FastGraphConfig.EDGESTORE_DEFAULT_BLOCKS];
        this.blocks[0] = new EdgeBlock(0);
        this.currentBlock = blocks[currentBlockIndex];
        this.dictionary = new Long2IntOpenHashMap(FastGraphConfig.EDGESTORE_BLOCK_SIZE);
        this.dictionary.defaultReturnValue(NULL_ID);
    }

    public EdgeImpl get(int id) {
        checkValidId(id);

        return blocks[id / FastGraphConfig.EDGESTORE_BLOCK_SIZE].get(id);
    }

    public EdgeImpl get(final Object id) {
        checkNonNullObject(id);

        int index = dictionary.get(id);
        if (index != EdgeStore.NULL_ID) {
            return get(index);
        }
        return null;
    }

    protected static long getLongId(NodeImpl source, NodeImpl target, boolean directed) {
        if (directed) {
            long edgeId = ((long) source.storeId) << NODE_BITS;
            edgeId = edgeId | (long) (target.storeId);
            return edgeId;
        } else {
            long edgeId = ((long) (source.storeId > target.storeId ? source.storeId : target.storeId)) << NODE_BITS;
            edgeId = edgeId | (long) (source.storeId > target.storeId ? target.storeId : source.storeId);
            return edgeId;
        }
    }

    private void ensureCapacity(final int capacity) {
        assert capacity > 0;

        int blockCapacity = currentBlock.getCapacity();
        while (capacity > blockCapacity) {
            if (currentBlockIndex == blocksCount - 1) {
                int blocksNeeded = (int) Math.ceil((capacity - blockCapacity) / (double) FastGraphConfig.EDGESTORE_BLOCK_SIZE);
                for (int i = 0; i < blocksNeeded; i++) {
                    if (blocksCount == blocks.length) {
                        EdgeBlock[] newBlocks = new EdgeBlock[blocksCount + 1];
                        System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
                        blocks = newBlocks;
                    }
                    EdgeBlock block = blocks[blocksCount];
                    if (block == null) {
                        block = new EdgeBlock(blocksCount);
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
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        checkNonNullEdgeObject(o);

        EdgeImpl edge = (EdgeImpl) o;
        int id = edge.getStoreId();
        if (id != EdgeStore.NULL_ID) {
            if (get(id) == edge) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Edge> iterator() {
        return new EdgeStoreIterator();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(final Edge e) {
        checkNonNullEdgeObject(e);

        EdgeImpl edge = (EdgeImpl) e;
        if (edge.storeId == EdgeStore.NULL_ID) {
            checkIdDoesntExist(e.getId());
            checkSourceTargets(edge);

            boolean directed = edge.isDirected();
            NodeImpl source = edge.source;
            NodeImpl target = edge.target;

            incrementVersion();

            if (garbageSize > 0) {
                for (int i = 0; i < blocksCount; i++) {
                    EdgeBlock edgeBlock = blocks[i];
                    if (edgeBlock.hasGarbage()) {
                        edgeBlock.set(edge);
                        garbageSize--;
                        dictionary.put(edge.getId(), edge.storeId);
                        break;
                    }
                }
            } else {
                ensureCapacity(1);
                currentBlock.add(edge);
                dictionary.put(edge.getId(), edge.storeId);
            }

            source.outDegree++;
            target.inDegree++;

            if (directed && !edge.isSelfLoop()) {
                EdgeImpl mutual = getMutual(edge);
                if (mutual != null) {
                    edge.setMutual(true);
                    mutual.setMutual(true);
                    source.mutualDegree++;
                    target.mutualDegree++;
                    mutualEdgesSize++;
                }
            }

            if (!directed) {
                undirectedSize++;
            }

            size++;
            return true;
        } else if (isValidIndex(edge.storeId) && get(edge.storeId) == edge) {
            return false;
        } else {
            throw new IllegalArgumentException("The edge already belongs to another store");
        }
    }

    private EdgeImpl getMutual(final EdgeImpl edge) {
        return get(edge.target, edge.source);
    }

    public EdgeImpl get(final Node source, final Node target) {
        checkNonNullObject(source);
        checkNonNullObject(target);
        NodeImpl sourceImpl = (NodeImpl) source;
        NodeImpl targetImpl = (NodeImpl) target;

        int index = dictionary.get(getLongId(sourceImpl, targetImpl, false));
        if (index != NULL_ID) {
            return get(index);
        }

        return null;
    }

    @Override
    public boolean remove(Object o) {
        checkNonNullEdgeObject(o);

        EdgeImpl edge = (EdgeImpl) o;
        int id = edge.storeId;
        if (id != EdgeStore.NULL_ID) {
            checkEdgeExists(edge);

            incrementVersion();

            edge.clearAttributes();

            int storeIndex = id / FastGraphConfig.EDGESTORE_BLOCK_SIZE;
            EdgeBlock block = blocks[storeIndex];
            block.remove(edge);

            boolean directed = edge.isDirected();
            NodeImpl source = edge.source;
            NodeImpl target = edge.target;

            source.outDegree--;
            target.inDegree--;

            size--;
            garbageSize++;
            dictionary.remove(edge.getId());
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

            if (directed && !edge.isSelfLoop()) {
                EdgeImpl mutual = getMutual(edge);
                if (mutual != null) {
                    edge.setMutual(false);
                    mutual.setMutual(false);
                    source.mutualDegree--;
                    target.mutualDegree--;
                    mutualEdgesSize--;
                }
            }

            if (!directed) {
                undirectedSize--;
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
                if (contains((EdgeImpl) o)) {
                    found++;
                }
            }
            return found == c.size();
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Edge> c) {
        checkCollection(c);

        if (!c.isEmpty()) {
            int capacityNeeded = c.size() - garbageSize;
            if (capacityNeeded > 0) {
                ensureCapacity(capacityNeeded);
            }
            boolean changed = false;
            Iterator<? extends Edge> itr = c.iterator();
            while (itr.hasNext()) {
                Edge e = itr.next();
                if (add(e)) {
                    changed = true;
                }
            }
            return changed;
        }
        return false;
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
            ObjectSet<EdgeImpl> set = new ObjectOpenHashSet(c.size());
            for (Object o : c) {
                checkNonNullObject(o);
                checkEdgeExists((EdgeImpl) o);
                set.add((EdgeImpl) o);
            }

            boolean changed = false;
            Iterator<Edge> itr = iterator();
            while (itr.hasNext()) {
                EdgeImpl e = (EdgeImpl) itr.next();
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

        for (EdgeStoreIterator itr = new EdgeStoreIterator(); itr.hasNext();) {
            EdgeImpl edge = itr.next();
            edge.setStoreId(EdgeStore.NULL_ID);
        }
        initStore();
    }

    @Override
    public Edge[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Edge> toCollection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void checkValidId(final int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Edge id=" + id + " is invalid");
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.size;
        EdgeStoreIterator itr = (EdgeStoreIterator) this.iterator();
        while (itr.hasNext()) {
            hash = 67 * hash + itr.next().hashCode();
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
        final EdgeStore other = (EdgeStore) obj;
        if (this.size != other.size) {
            return false;
        }
        EdgeStoreIterator itr1 = (EdgeStoreIterator) this.iterator();
        EdgeStoreIterator itr2 = (EdgeStoreIterator) other.iterator();
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

    protected class EdgeStoreIterator implements Iterator<Edge> {

        protected int blockIndex;
        protected EdgeImpl[] backingArray;
        protected int blockLength;
        protected int cursor;
        protected EdgeImpl pointer;

        public EdgeStoreIterator() {
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
        public EdgeImpl next() {
            return pointer;
        }

        @Override
        public void remove() {
            EdgeStore.this.remove(pointer);
        }
    }

    void checkCollection(final Collection<?> collection) {
        if (collection == this) {
            throw new IllegalArgumentException("Can't pass itself");
        }
    }

    void checkNonNullObject(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    void checkValidNodeObject(final Node n) {
        if (n == null) {
            throw new NullPointerException();
        }
        if (!(n instanceof NodeImpl)) {
            throw new ClassCastException("Object must be a NodeImpl object");
        }
    }

    void checkNonNullEdgeObject(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof EdgeImpl)) {
            throw new ClassCastException("Object must be a EdgeImpl object");
        }
    }

    void checkSourceTargets(final EdgeImpl e) {
        if (e.source == null || e.target == null) {
            throw new NullPointerException();
        }
    }

    protected static class EdgeBlock {

        protected final int offset;
        protected final short[] garbageArray;
        protected final EdgeImpl[] backingArray;
        protected int nodeLength;
        protected int garbageLength;

        public EdgeBlock(int index) {
            this.offset = index * FastGraphConfig.EDGESTORE_BLOCK_SIZE;
            if (FastGraphConfig.EDGESTORE_BLOCK_SIZE >= Short.MAX_VALUE - Short.MIN_VALUE) {
                throw new RuntimeException("BLOCK SIZE can't exceed 65535");
            }
            this.garbageArray = new short[FastGraphConfig.EDGESTORE_BLOCK_SIZE];
            this.backingArray = new EdgeImpl[FastGraphConfig.EDGESTORE_BLOCK_SIZE];
        }

        public boolean hasGarbage() {
            return garbageLength > 0;
        }

        public int getCapacity() {
            return FastGraphConfig.EDGESTORE_BLOCK_SIZE - nodeLength - garbageLength;
        }

        public void add(EdgeImpl k) {
            int i = nodeLength++;
            backingArray[i] = k;
            k.setStoreId(i + offset);
        }

        public void set(EdgeImpl k) {
            int i = garbageArray[--garbageLength] - Short.MIN_VALUE;
            backingArray[i] = k;
            k.setStoreId(i + offset);
        }

        public EdgeImpl get(int id) {
            return backingArray[id - offset];
        }

        public void remove(EdgeImpl k) {
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

    void checkIdDoesntExist(Object id) {
        if (dictionary.containsKey(id)) {
            throw new IllegalArgumentException("The node id already exist");
        }
    }

    private void incrementVersion() {
        if (version != null) {
            version.incrementAndGetEdgeVersion();
        }
    }

    private void trimDictionary() {
        dictionary.trim(Math.max(FastGraphConfig.EDGESTORE_BLOCK_SIZE, size * 2));
    }

    boolean isValidIndex(int id) {
        return id >= 0 && id < currentBlock.offset + currentBlock.nodeLength;
    }

    void checkEdgeExists(final EdgeImpl edge) {
        if (get(edge.storeId) != edge) {
            throw new IllegalArgumentException("The edge is invalid");
        }
    }

    public boolean isAdjacent(Node node1, Node node2) {
        checkValidNodeObject(node1);
        checkValidNodeObject(node2);

        int typeLength = dictionary.size();
        for (int i = 0; i < typeLength; i++) {
            if (contains((NodeImpl) node1, (NodeImpl) node2)) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(NodeImpl source, NodeImpl target) {
        checkNonNullObject(source);
        checkNonNullObject(target);

        return dictionary.containsKey(getLongId(source, target, true));
    }

}
