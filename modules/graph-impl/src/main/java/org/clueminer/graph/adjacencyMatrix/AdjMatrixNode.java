/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import org.clueminer.dataset.api.Instance;
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
    private Instance instance;

    public AdjMatrixNode(long id) {
        this.id = id;
    }

    public AdjMatrixNode(long id, Object label) {
        this.label = label;
        this.id = id;
    }

    public AdjMatrixNode(long id, Instance i) {
        instance = i;
        this.id = id;
    }

    @Override
    public void setInstance(Instance i) {
        instance = i;
    }

    @Override
    public Instance getInstance() {
        return instance;
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
