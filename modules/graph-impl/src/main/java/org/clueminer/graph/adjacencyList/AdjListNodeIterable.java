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
        this.nodes = nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public Node[] toArray() {
        return nodes.toArray(new Node[0]);
    }

    @Override
    public Collection<Node> toCollection() {
        return nodes;
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return 0;
    }

}
