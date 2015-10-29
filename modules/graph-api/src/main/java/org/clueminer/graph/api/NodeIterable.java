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
package org.clueminer.graph.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A node iterable.
 */
public interface NodeIterable extends ElementIterable<Node> {

    /**
     * The static empty iterable.
     */
    static final NodeIterable EMPTY = new NodeIterableEmpty();

    /**
     * Returns a node iterator.
     *
     * @return node iterator
     */
    @Override
    Iterator<Node> iterator();

    /**
     * Returns the iterator content as an array.
     *
     * @return node array
     */
    @Override
    Node[] toArray();

    /**
     * Returns the iterator content as a collection.
     *
     * @return node collection
     */
    @Override
    Collection<Node> toCollection();

    static final class NodeIterableEmpty implements Iterator<Node>, NodeIterable {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Node next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Iterator<Node> iterator() {
            return this;
        }

        @Override
        public Node[] toArray() {
            return new Node[0];
        }

        @Override
        public Collection<Node> toCollection() {
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
