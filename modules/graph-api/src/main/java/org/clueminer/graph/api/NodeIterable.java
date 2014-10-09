/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public Iterator<Node> iterator();

    /**
     * Returns the iterator content as an array.
     *
     * @return node array
     */
    @Override
    public Node[] toArray();

    /**
     * Returns the iterator content as a collection.
     *
     * @return node collection
     */
    @Override
    public Collection<Node> toCollection();

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
    }
}
