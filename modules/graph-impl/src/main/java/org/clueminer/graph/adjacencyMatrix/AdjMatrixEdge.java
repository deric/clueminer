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

    private long id;
    private final AdjMatrixNode source;
    private final AdjMatrixNode target;
    private boolean directed;
    private final double weight;
    
    AdjMatrixEdge(long id,Node source, Node target, double weight) {
        this.id = id;
        this.source = (AdjMatrixNode) source;
        this.target = (AdjMatrixNode) target;
        this.weight = weight;
    }
    
    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public AdjMatrixNode getSource() {
        return source;
    }

    @Override
    public AdjMatrixNode getTarget() {
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
}
