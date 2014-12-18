package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListFactory implements GraphFactory {

    private static AdjListFactory instance;

    private static long nodeIdCounter;
    private static long edgeIdCounter;
    
    public static AdjListFactory getInstance() {
        if (instance == null) {
            instance = new AdjListFactory();
        }
        return instance;
    }

    protected AdjListFactory() {
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
	public Node newNode(Object label) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<Node> createNodesFromInput(Dataset<? extends Instance> input) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


}
