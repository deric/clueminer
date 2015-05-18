package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author tomas
 */
public class AdjMatrixEdgeIterable implements EdgeIterable {

    private final ArrayList<Edge> edges;

    public AdjMatrixEdgeIterable(Edge[] edges) {
        this.edges = new ArrayList<>(Arrays.asList(edges));
    }

    public AdjMatrixEdgeIterable(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public Iterator<Edge> iterator() {
        return new AdjMatrixEdgeIterator();
    }

    @Override
    public Edge[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Edge> toCollection() {
        return edges;
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return edges.size();
    }

    private class AdjMatrixEdgeIterator implements Iterator<Edge> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < edges.size();
        }

        @Override
        public Edge next() {
            return edges.get(currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
