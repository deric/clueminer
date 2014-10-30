/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;


import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class AdjMatrixFactory implements GraphFactory {

    private static AdjMatrixFactory instance;
    

    public static AdjMatrixFactory getInstance() {
        if (instance == null) {
            instance = new AdjMatrixFactory();
        }
        return instance;
    }

    protected AdjMatrixFactory() {
        
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
        Edge edge = new AdjMatrixEdge(source, target, weight);
        return edge;
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode(Object id) {
        Node node = new AdjMatrixNode(id);
        return node;
    }

    public Node newNode(Object id, int dimension) {
        Node node = new AdjMatrixNode(id, dimension);
        return node;
    }

    public Node newNode(Object id, double[] coordinates) {
        Node node = new AdjMatrixNode(id, coordinates);
        return node;
    }
}
