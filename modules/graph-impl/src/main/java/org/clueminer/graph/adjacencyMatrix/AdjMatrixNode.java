/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class AdjMatrixNode implements Node {

    private final Object id;
    private int number;

    public AdjMatrixNode(Object id) {
        this.id = id;
        this.number = -1;
    }

    @Override
    public Object getId() {
        return id;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
}
