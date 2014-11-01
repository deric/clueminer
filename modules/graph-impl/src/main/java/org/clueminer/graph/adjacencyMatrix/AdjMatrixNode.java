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
    private int index;
    private double[] coordinates;

    public AdjMatrixNode(Object id) {
        this.id = id;
        this.index = -1;
    }
    
    public AdjMatrixNode(Object id, int dimension) {
        coordinates = new double[dimension];
        this.id = id;
    }
    
    public AdjMatrixNode(Object id, double[] coordinates) {
        this.coordinates = coordinates;
        this.id = id;
    }

    public void setCoordinate(int i, int value) {
        coordinates[i] = value;
    }
    
    public double getCoordinate(int i) {
        return coordinates[i];
    }
    
    @Override
    public Object getId() {
        return id;
    }
    
    @Override
    public int getIndex() {
        return index;
    }
    
    @Override
    public void setIndex(int index) {
        this.index = index;
    }
    
}