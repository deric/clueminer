package org.clueminer.graph.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An edge iterable.
 */
public interface EdgeIterable extends ElementIterable<Edge> {

    /**
     * The static empty iterable.
     */
    public static final EdgeIterable EMPTY = new EdgeIterableEmpty();

    /**
     * Returns an edge iterator.
     *
     * @return edge iterator
     */
    @Override
    Iterator<Edge> iterator();

    /**
     * Returns the iterator content as an array.
     *
     * @return edge array
     */
    @Override
    Edge[] toArray();

    /**
     * Returns the iterator content as a collection.
     *
     * @return edge collection
     */
    @Override
    public Collection<Edge> toCollection();

    static final class EdgeIterableEmpty implements Iterator<Edge>, EdgeIterable {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Edge next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Iterator<Edge> iterator() {
            return this;
        }

        @Override
        public Edge[] toArray() {
            return new Edge[0];
        }

        @Override
        public Collection<Edge> toCollection() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public void doBreak() {
        }

        @Override
        public int size() {
            return 0;
        }
    }
}
