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
    Iterator<T> iterator();

    /**
     * Returns the iterator content as an array.
     *
     * @return edge array
     */
    T[] toArray();

    /**
     * Returns the iterator content as a collection.
     *
     * @return edge array
     */
    Collection<T> toCollection();

    /**
     * Iterator size, if available
     *
     * @return
     */
    int size();

    /**
     * Break the iterator and release read lock (if any).
     */
    void doBreak();

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

        @Override
        public int size() {
            return 0;
        }
    }
}
