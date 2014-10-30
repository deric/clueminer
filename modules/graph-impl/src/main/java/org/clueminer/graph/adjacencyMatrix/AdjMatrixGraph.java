package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author Tomas Bruna
 */
public class AdjMatrixGraph implements org.clueminer.graph.api.Graph {

    AdjMatrixEdge[][] adjMatrix;
    AdjMatrixNode[] nodes;
    int size;
    int nodeCounter;

    public AdjMatrixGraph(int size) {
        this.size = size;
        this.nodes = new AdjMatrixNode[size];
        nodeCounter = 0;
        adjMatrix = new AdjMatrixEdge[size][size];
    }

    @Override
    public boolean addEdge(Edge edge) {
        AdjMatrixEdge e = (AdjMatrixEdge) edge;
        int source = e.getSource().getNumber();
        int target = e.getTarget().getNumber();
        if (source == -1 || target == -1 || source >= nodeCounter || target >= nodeCounter) {
            return false;
        }
        adjMatrix[source][target] = e;
        adjMatrix[target][source] = e;
        return true;
    }

    @Override
    public boolean addNode(Node node) {
        AdjMatrixNode n = (AdjMatrixNode) node;
        if (nodeCounter >= size) {
            return false;
        }
        n.setNumber(nodeCounter);
        nodes[nodeCounter++] = n;
        return true;
    }

    @Override
    public boolean addAllEdges(Collection<? extends Edge> edges) {
        for (Edge edge : edges) {
            if (!addEdge(edge)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAllNodes(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            if (!addNode(node)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeEdge(Edge edge) {
        AdjMatrixEdge e = (AdjMatrixEdge) edge;
        int source = e.getSource().getNumber();
        int target = e.getTarget().getNumber();
        adjMatrix[source][target] = adjMatrix[target][source] = null;
        return true;
    }

    @Override
    public boolean removeNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Edge> edges) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAllNodes(Collection<? extends Node> nodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Node node) {
        AdjMatrixNode n = (AdjMatrixNode) node;
        return n.getNumber() < size;
    }

    @Override
    public boolean contains(Edge edge) {
        AdjMatrixEdge e = (AdjMatrixEdge) edge;
        int source = e.getSource().getNumber();
        int target = e.getTarget().getNumber();
        if (source >= size || target >= size) {
            return false;
        }
        return adjMatrix[source][target] != null;
    }

    @Override
    public Node getNode(Object id) {
        for (int i = 0; i < nodeCounter; i++) {
            if (nodes[i].getId() == id) {
                return nodes[i];
            }
        }
        return null;
    }

    @Override
    public Edge getEdge(Object id) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (adjMatrix[i][j] != null && id == adjMatrix[i][j].getId()) {
                    return adjMatrix[i][j];
                }
            }
        }
        return null;
    }

    @Override
    public Edge getEdge(Node node1, Node node2) {
        AdjMatrixNode n1 = (AdjMatrixNode) node1;
        AdjMatrixNode n2 = (AdjMatrixNode) node2;
        return adjMatrix[n1.getNumber()][n2.getNumber()];
    }

    @Override
    public Edge getEdge(Node node1, Node node2, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeIterable getNodes() {
        return new AdjMatrixNodeIterable(nodes);
    }

    @Override
    public EdgeIterable getEdges() {
        //for undirected edges only
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = i; j < nodeCounter; j++) {
                if (adjMatrix[i][j] != null) {
                    edges.add(adjMatrix[i][j]);
                }
            }
        }
        return new AdjMatrixEdgeIterable(edges);
    }

    @Override
    public EdgeIterable getSelfLoops() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeIterable getNeighbors(Node node) {
        AdjMatrixNode n = (AdjMatrixNode) node;
        int nodeNumber = n.getNumber();
        ArrayList<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < nodeCounter; i++) {
            if (adjMatrix[i][nodeNumber] != null) {
                neighbours.add(nodes[i]);
            }
        }
        return new AdjMatrixNodeIterable(neighbours);
    }

    @Override
    public NodeIterable getNeighbors(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EdgeIterable getEdges(Node node) {
        //for undirected edges only
        AdjMatrixNode n = (AdjMatrixNode) node;
        int nodeNumber = n.getNumber();
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodeCounter; i++) {
            if (adjMatrix[i][nodeNumber]!=null) {
                edges.add(adjMatrix[i][nodeNumber]);
            }
        }
        return new AdjMatrixEdgeIterable(edges);
    }

    @Override
    public EdgeIterable getEdges(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNodeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEdgeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEdgeCount(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getOpposite(Node node, Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDegree(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSelfLoop(Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDirected(Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAdjacent(Node node1, Node node2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAdjacent(Node node1, Node node2, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIncident(Edge edge1, Edge edge2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIncident(Node node, Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearEdges(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearEdges(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearEdges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDirected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isUndirected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMixed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String graphVizExport() {
        String result = "Graph G {\n";
        result += exportNodes();
        result += exportEdges();
        result += "}";
        return result;
    }

    public String exportNodes() {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            result += "    " + nodes[i].getNumber() + "[fontsize=11 pos=\"" + nodes[i].getCoordinate(0) + ","
                    + nodes[i].getCoordinate(1) + "!\" width=0.1 height=0.1 shape=point];\n";
        }
        return result;
    }

    public String exportEdges() {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = i; j < nodeCounter; j++) {
                if (adjMatrix[i][j] != null) {
                    result += "    " + adjMatrix[i][j].getSource().getNumber() + " -- " + adjMatrix[i][j].getTarget().getNumber() + ";\n";
                }
            }
        }
        return result;
    }
    
}
