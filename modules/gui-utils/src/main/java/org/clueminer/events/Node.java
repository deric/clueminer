package org.clueminer.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A node structure for DAG (directed acyclic graph)
 *
 * @author Tomas Barton
 */
public class Node<T> implements Iterable<Node<T>> {

    private final T value;
    private final List<Node<T>> outEdges = new LinkedList<>();
    private final List<Node<T>> inEdges = new LinkedList<>();

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * Add outgoing edge
     *
     * @param otherNode
     */
    public void addOutEdge(Node<T> otherNode) {
        if (this.containsSucc(otherNode)) {
            throw new RuntimeException("cycle detected");
        }
        outEdges.add(otherNode);
        otherNode.addInEdge(this);
    }

    public void addInEdge(Node<T> parentNode) {
        inEdges.add(parentNode);
    }

    public void removeOutEdge(Node<T> otherNode) {
        outEdges.remove(otherNode);
        otherNode.removeInEdge(this);
    }

    public void removeInEdge(Node<T> node) {
        inEdges.remove(node);
    }

    public int outEdgesCnt() {
        return outEdges.size();
    }

    public int inEdgesCnt() {
        return inEdges.size();
    }

    /**
     *
     * @param node
     * @return true if any connected node containsSucc given node
     */
    public boolean containsSucc(Node<T> node) {
        if (node.equals(this)) {
            return true;
        }
        for (Node<T> other : outEdges) {
            //recursive
            if (other.equals(node) || other.containsSucc(node)) {
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
            return index < outEdgesCnt();
        }

        @Override
        public Node<T> next() {
            return outEdges.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from nodes using the iterator.");

        }
    }
}
