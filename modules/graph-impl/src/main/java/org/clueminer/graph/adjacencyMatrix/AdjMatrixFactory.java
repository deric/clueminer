/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class AdjMatrixFactory implements GraphFactory {

    private static AdjMatrixFactory instance;

    private static long nodeIdCounter;
    private static long edgeIdCounter;

    public static AdjMatrixFactory getInstance() {
        if (instance == null) {
            instance = new AdjMatrixFactory();
        }
        return instance;
    }

    protected AdjMatrixFactory() {
        nodeIdCounter = edgeIdCounter = 0;
    }

    @Override
    public Edge newEdge(Node source, Node target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Edge newEdge(Node source, Node target, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, double weight, boolean directed) {
        return new AdjMatrixEdge(edgeIdCounter++, source, target, weight);
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode() {
        return new AdjMatrixNode(nodeIdCounter++);
    }

    @Override
    public Node newNode(Object label) {
        return new AdjMatrixNode(nodeIdCounter++, label);
    }

    @Override
    public Node newNode(Instance i) {
        return new AdjMatrixNode(nodeIdCounter++, i);
    }

    @Override
    public ArrayList<Node> createNodesFromInput(Dataset<? extends Instance> input) {
        ArrayList<Node> nodes = new ArrayList<>(input.size());
        for (Instance ins : input) {
            nodes.add(newNode(ins));
        }
        return nodes;
    }

}
