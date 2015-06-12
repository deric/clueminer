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
package org.clueminer.graph.fast;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
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

    protected final static int NULL_ID = -1;

    private int size;
    private Long2IntOpenHashMap idMap;
    private EdgeImpl[] store;

    public EdgeStore() {
        initialize();
    }

    private void initialize() {
        size = 0;
        int capacity = 10;
        //initial capacity
        idMap = new Long2IntOpenHashMap(capacity);
        store = new EdgeImpl[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public final void ensureCapacity(int requested) {
        if (requested >= store.length) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = size() * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            if (capacity > store.length) {
                EdgeImpl[] tmp = new EdgeImpl[capacity];
                System.arraycopy(store, 0, store, 0, store.length);
                store = tmp;
            }
        }
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof EdgeImpl)) {
            return false;
        }
        return idMap.containsKey(o);
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
    public boolean add(Edge e) {
        checkNonNullEdgeObject(e);

        EdgeImpl edge = (EdgeImpl) e;

        checkSourceTargets(edge);

        NodeImpl source = (NodeImpl) edge.source;
        NodeImpl target = (NodeImpl) edge.target;

        ensureCapacity(size() + 1);
        store[size] = edge;
        idMap.put(edge.getId(), size);

        source.outDegree++;
        target.inDegree++;

        size++;
        return true;

    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends Edge> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        initialize();
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

        }

        @Override
        public boolean hasNext() {
            pointer = null;
            while (cursor >= store.length) {
                pointer = store[cursor++];
                if (cursor >= store.length || pointer == null) {
                    break;
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

}
