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
        Edge edge = new AdjMatrixEdge(edgeIdCounter++, source, target, weight);
        return edge;
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode() {
        Node node = new AdjMatrixNode(nodeIdCounter++);
        return node;
    }

    @Override
    public Node newNode(Object label) {
        Node node = new AdjMatrixNode(nodeIdCounter++, label);
        return node;
    }

    @Override
    public Node newNode(Instance i) {
        Node node = new AdjMatrixNode(nodeIdCounter++, i);
        return node;
    }

    @Override
    public ArrayList<Node> createNodesFromInput(Dataset<? extends Instance> input) {
        ArrayList<Node> nodes = new ArrayList<>(input.size());
        for (Instance ins : input) {
            nodes.add((AdjMatrixNode) newNode(ins));
        }
        return nodes;
    }

}
