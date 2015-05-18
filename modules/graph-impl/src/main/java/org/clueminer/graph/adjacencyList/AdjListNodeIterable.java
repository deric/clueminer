package org.clueminer.graph.adjacencyList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListNodeIterable implements NodeIterable {

    private final List<Node> nodes;

    public AdjListNodeIterable(Map<Long, Node> nodes) {
        this.nodes = new LinkedList<>(nodes.values());
    }

    public AdjListNodeIterable(List<Node> nodes) {
        this.nodes = new LinkedList<>(nodes);
    }

    @Override
    public Iterator<Node> iterator() {
        Iterator<Node> iterator = new Iterator<Node>() {

            private final Iterator<Node> it = nodes.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Node next() {
                return it.next();
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
        return iterator;
    }

    @Override
    public Node[] toArray() {
        Node[] array = new Node[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            array[i] = nodes.get(i);
        }
        return array;
    }

    @Override
    public Collection<Node> toCollection() {
        return new LinkedList<>(nodes);
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
