/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class AdjMatrixEdge implements Edge {

    private final long id;
    private final Node source;
    private final Node target;
    private boolean directed;
    private final double weight;
    private Object label;

    AdjMatrixEdge(long id,Node source, Node target, double weight) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.weight = weight;
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
        return this.label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }
}
