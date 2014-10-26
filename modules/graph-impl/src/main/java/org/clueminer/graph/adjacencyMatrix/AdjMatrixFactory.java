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
public class adjMatrixFactory implements GraphFactory {

    private static adjMatrixFactory instance;
    

    public static adjMatrixFactory getInstance() {
        if (instance == null) {
            instance = new adjMatrixFactory();
        }
        return instance;
    }

    protected adjMatrixFactory() {
        
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
        System.out.println(edge.getWeight());
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

}
