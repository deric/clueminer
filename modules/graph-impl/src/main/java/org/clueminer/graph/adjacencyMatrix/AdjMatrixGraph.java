package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.clueminer.distance.EuclideanDistance;
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

    HashMap<Long, Integer> idToIndex;
    HashMap<Long, AdjMatrixNode> idToNode;
    AdjMatrixEdge[][] adjMatrix;
    AdjMatrixNode[] nodes;
    int size;
    int nodeCounter;
    DistanceMeasure dm;

    public AdjMatrixGraph(int size) {
        this(size, new EuclideanDistance());
    }

    public AdjMatrixGraph(int size, DistanceMeasure dm) {
        this.size = size;
        this.nodes = new AdjMatrixNode[size];
        nodeCounter = 0;
        adjMatrix = new AdjMatrixEdge[size][size];
        idToIndex = new HashMap<>();
        idToNode = new HashMap<>();
        this.dm = dm;
    }

    @Override
    public boolean addEdge(Edge edge) {
        AdjMatrixEdge e = (AdjMatrixEdge) edge;
        int source = idToIndex.get(e.getSource().getId());
        int target = idToIndex.get(e.getTarget().getId());
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
        idToNode.put(node.getId(), (AdjMatrixNode) node);
        idToIndex.put(node.getId(), nodeCounter);
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
        int source = idToIndex.get(edge.getSource().getId());
        int target = idToIndex.get(edge.getTarget().getId());
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
        return idToNode.get(node.getId()) != null;
    }

    @Override
    public boolean contains(Edge edge) {
        int source = idToIndex.get(edge.getSource().getId());
        int target = idToIndex.get(edge.getTarget().getId());
        if (source >= size || target >= size) {
            return false;
        }
        return adjMatrix[source][target] != null;
    }

    @Override
    public Node getNode(long id) {
        return idToNode.get(id);
    }

    @Override
    public Edge getEdge(long id) {
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
        return adjMatrix[idToIndex.get(node1.getId())][idToIndex.get(node2.getId())];
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
        int index = idToIndex.get(node.getId());
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
        int index = idToIndex.get(node.getId());
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
        int index = idToIndex.get(node.getId());
        for (int i = 0; i < nodeCounter; i++) {
            if (i != index && adjMatrix[i][index] != null) {
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
        return adjMatrix[idToIndex.get(node1.getId())][idToIndex.get(node2.getId())] != null;
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
     * Export graph to Graphviz * To build graph run "neato -Tpng -o out.png
     * -Gmode=KK input" where input is the output of this function
     *
     * @return Graphviz source code describing this graph
     */
    public String graphVizExport(double scale) {
        String result = "Graph G {\n";
        result += exportNodes(scale);
        result += exportEdges();
        result += "}\n";
        return result;
    }

    /* add to parent class */
    public String exportNodes(double scale) {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            result += "    " + i + "[fontsize=11 pos=\"" + nodes[i].getInstance().get(0) * scale + ","
                    + nodes[i].getInstance().get(1) * scale + "!\" width=0.1 height=0.1 shape=point];\n";
        }
        return result;
    }

    public String exportEdges() {
        String result = "";
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = i; j < nodeCounter; j++) {
                if (adjMatrix[i][j] != null) {
                    result += "    " + idToIndex.get(adjMatrix[i][j].getSource().getId()) + " -- " + idToIndex.get(adjMatrix[i][j].getTarget().getId()) + ";\n";
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
        if (k > nodeCounter) {
            return false;
        }
        AdjMatrixFactory f = AdjMatrixFactory.getInstance();
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = 0; j < k; j++) {
                double distance = dm.measure(nodes[i].getInstance(), nodes[neighbors[i][j]].getInstance());
                if (distance < 0.1) {
                    distance = 0.1;
                }
                addEdge((AdjMatrixEdge) f.newEdge(nodes[i], nodes[neighbors[i][j]], 1, 1 / distance, false)); //max val
            }
        }
        return true;
    }

    @Override
    public GraphFactory getFactory() {
        return AdjMatrixFactory.getInstance();
    }

    @Override
    public int getIndex(Node node) {
        return idToIndex.get(node.getId());
    }

}
