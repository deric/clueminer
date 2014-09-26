package org.clueminer.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public class Node<T> implements Iterable<Node<T>> {

    private final T value;
    private final List<Node<T>> edges = new LinkedList<>();

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void addEdge(Node<T> otherNode) {
        if (this.contains(otherNode)) {
            throw new RuntimeException("cycle detected");
        }
        edges.add(otherNode);
    }

    public void removeEdge(Node<T> otherNode) {
        edges.remove(otherNode);
    }

    public int edgesCnt() {
        return edges.size();
    }

    /**
     *
     * @param node
     * @return true if any connected node contains given node
     */
    public boolean contains(Node<T> node) {
        if (node.equals(this)) {
            return true;
        }
        for (Node<T> other : edges) {
            //recursive
            if (other.equals(node) || other.contains(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new NodeIterator();
    }

    class NodeIterator implements Iterator<Node<T>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < edgesCnt();
        }

        @Override
        public Node<T> next() {
            return edges.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from nodes using the iterator.");

        }
    }
}
