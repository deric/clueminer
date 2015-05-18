package org.clueminer.graph.adjacencyList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeIterable implements EdgeIterable {

    private final List<Edge> edges;

    public AdjListEdgeIterable(Map<Long, Edge> edges) {
        this.edges = new LinkedList<>(edges.values());
    }

    public AdjListEdgeIterable(List<Edge> edges) {
        this.edges = new LinkedList<>(edges);
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }

    @Override
    public Edge[] toArray() {
        Edge[] array = new Edge[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            array[i] = edges.get(i);
        }
        return array;
    }

    @Override
    public Collection<Edge> toCollection() {
        return new LinkedList<>(edges);
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
