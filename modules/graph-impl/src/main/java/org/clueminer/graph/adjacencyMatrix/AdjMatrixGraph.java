package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.GraphFactory;
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
        int source = e.getSource().getIndex();
        int target = e.getTarget().getIndex();
        if (source == -1 || target == -1 || source >= nodeCounter || target >= nodeCounter) {
            return false;
        }
        adjMatrix[source][target] = e;
        adjMatrix[target][source] = e;
        return true;
    }

    @Override
    public boolean addNode(Node node) {
        if (nodeCounter >= size) {
            return false;
        }
        node.setIndex(nodeCounter);
        nodes[nodeCounter++] = (AdjMatrixNode) node;
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
        int source = edge.getSource().getIndex();
        int target = edge.getTarget().getIndex();
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
        return n.getIndex() < size;
    }

    @Override
    public boolean contains(Edge edge) {
        int source = edge.getSource().getIndex();
        int target = edge.getTarget().getIndex();
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
        return adjMatrix[node1.getIndex()][node2.getIndex()];
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
        int index = node.getIndex();
        ArrayList<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < nodeCounter; i++) {
            if (i != index && adjMatrix[i][index] != null) {
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
        int index = node.getIndex();
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodeCounter; i++) {
            if (adjMatrix[i][index] != null) {
                edges.add(adjMatrix[i][index]);
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
        return nodeCounter;
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
        int degree = 0;
        int index = node.getIndex();
        for (int i = 0; i < nodeCounter; i++) {
            if (i!=index && adjMatrix[i][index] != null) {
                degree++;
            }
        }
        return degree; 
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

        
     /**
     * Export graph to Graphviz
     * 
     * To build graph run "neato -Tpng -o out.png -Gmode=KK input" 
     * where input is the output of this function
     * 
     * @return Graphviz source code describing this graph
     */
    public String graphVizExport() {
        String result = "Graph G {\n";
        result += exportNodes();
        result += exportEdges();
        result += "}\n";
        return result;
    }

    /* add to parent class */
    public String exportNodes() {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            result += "    " + nodes[i].getIndex() + "[fontsize=11 pos=\"" + nodes[i].getCoordinate(0) + ","
                    + nodes[i].getCoordinate(1) + "!\" width=0.1 height=0.1 shape=point];\n";
        }
        return result;
    }

    public String exportEdges() {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = i; j < nodeCounter; j++) {
                if (adjMatrix[i][j] != null) {
                    result += "    " + adjMatrix[i][j].getSource().getIndex() + " -- " + adjMatrix[i][j].getTarget().getIndex() + ";\n";
                }
            }
        }
        return result;
    }
    
     /**
     * Create edges in graph according to array of neighbors
     *
     * @param neighbors neighbor array
     * @param k number of neighbors for each node
     */
    @Override
    public boolean addEdgesFromNeigborArray(int[][] neighbors, int k) {
        if (k>nodeCounter) {
            return false;
        }
        AdjMatrixFactory f = AdjMatrixFactory.getInstance();
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = 0; j < k; j++) {
                //todo - support distanceMeasure for nodes
                double x1 = nodes[i].getCoordinate(0);
                double y1 = nodes[i].getCoordinate(1);
                double x2 = nodes[neighbors[i][j]].getCoordinate(0);
                double y2 = nodes[neighbors[i][j]].getCoordinate(1);
                //different const needed?
                addEdge((AdjMatrixEdge) f.newEdge(nodes[i], nodes[neighbors[i][j]],1 ,10/Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) , false)); //max val
            }
        }
        return true;
    }
    
    @Override
    public GraphFactory getFactory() {
        return AdjMatrixFactory.getInstance();
    }


}
