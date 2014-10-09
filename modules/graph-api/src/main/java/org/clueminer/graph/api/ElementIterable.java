package org.clueminer.graph.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Element iterable.
 *
 * @param <T> the element class
 */
public interface ElementIterable<T extends Element> extends Iterable<T> {

    /**
     * Empty iterable.
     */
    final ElementIterable EMPTY = new ElementIterable.ElementIterableEmpty();

    /**
     * Returns the element iterator.
     *
     * @return the iterator.
     */
    @Override
    public Iterator<T> iterator();

    /**
     * Returns the iterator content as an array.
     *
     * @return edge array
     */
    public T[] toArray();

    /**
     * Returns the iterator content as a collection.
     *
     * @return edge array
     */
    public Collection<T> toCollection();

    /**
     * Break the iterator and release read lock (if any).
     */
    public void doBreak();

    static final class ElementIterableEmpty implements Iterator<Element>, ElementIterable {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Element next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Iterator<Element> iterator() {
            return this;
        }

        @Override
        public Element[] toArray() {
            return new Node[0];
        }

        @Override
        public Collection<Element> toCollection() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public void doBreak() {
        }
    }
}
