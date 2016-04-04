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
