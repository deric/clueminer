package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
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
        nodeIdCounter = 0;
        edgeIdCounter = 0;
    }

    @Override
    public Edge newEdge(Node source, Node target) {
        return new AdjListEdge(edgeIdCounter++, source, target);
    }

    @Override
    public Edge newEdge(Node source, Node target, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, double weight, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed, weight);
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode() {
        return new AdjListNode(nodeIdCounter++);
    }

    @Override
    public Node newNode(Object label) {
        return new AdjListNode(nodeIdCounter++, label);
    }

    @Override
    public ArrayList<Node> createNodesFromInput(Dataset<? extends Instance> input) {
        ArrayList<Node> nodes = new ArrayList<>(input.size());
        for (Instance inputInstance : input) {
            Node node = this.newNode();
            node.setInstance(inputInstance);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public Node newNode(Instance instance) {
        Node node = this.newNode();
        node.setInstance(instance);
        return node;
    }

    protected static long getNodeCount() {
        return nodeIdCounter;
    }

    protected static long getEdgeCount() {
        return edgeIdCounter;
    }

    @Override
    public Long[] createNodesFromInput(Dataset<? extends Instance> input, Graph graph) {
        Long[] mapping = new Long[input.size()];
        for (Instance inst : input) {
            Node node = this.newNode();
            mapping[inst.getIndex()] = node.getId();
            node.setInstance(inst);
            graph.addNode(node);
        }
        return mapping;
    }

}
