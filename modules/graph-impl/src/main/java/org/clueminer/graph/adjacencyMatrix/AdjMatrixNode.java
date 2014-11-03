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

    private final long id;
    Object label;
    private int index;
    private double[] coordinates;

    public AdjMatrixNode(long id) {
        this.id = id;
    }

    public AdjMatrixNode(long id, Object label) {
        this.label = label;
        this.id = id;
    }

    public AdjMatrixNode(long id, int dimension) {
        coordinates = new double[dimension];
        this.id = id;
    }

    public AdjMatrixNode(long id, double[] coordinates) {
        this.coordinates = coordinates;
        this.id = id;
    }

    public void setCoordinate(int i, double value) {
        coordinates[i] = value;
    }

    public double getCoordinate(int i) {
        return coordinates[i];
    }


    @Override
   public long getId() {
        return id;
    }

    @Override
    public Object getLabel() {
        return label;
    }

}
