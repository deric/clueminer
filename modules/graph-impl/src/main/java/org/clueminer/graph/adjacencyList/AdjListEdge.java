package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListEdge implements Edge {

    private final long id;
    private final Node source;
    private final Node target;
    private final double weight;
    private final boolean directed;

    AdjListEdge(long id, Node source, Node target) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.weight = 1.0;
        this.directed = false;
    }

    AdjListEdge(long id, Node source, Node target, boolean directed) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.weight = 1.0;
        this.directed = directed;
    }

    AdjListEdge(long id, Node source, Node target, boolean directed, double weight) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.directed = directed;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public Object getLabel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "e" + id + ": n" + source.getId() + " -(" + weight + ")-> n" + target.getId();
    }
}
