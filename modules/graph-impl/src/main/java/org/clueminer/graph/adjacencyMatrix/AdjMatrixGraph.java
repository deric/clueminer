package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Graph.class)
public class AdjMatrixGraph implements Graph {

    private HashMap<Long, Integer> idToIndex;
    private HashMap<Long, AdjMatrixNode> idToNode;
    private AdjMatrixEdge[][] adjMatrix;
    private AdjMatrixNode[] nodes;
    private int edgeCounter;
    private int size;
    private int nodeCounter;
    private DistanceMeasure dm;
    private final double EPS = 1e-6;
    private static final String name = "Adj Graph Matrix";

    /**
     * ensureCapacity(n) must be called before using this class!
     */
    public AdjMatrixGraph() {
        this.dm = EuclideanDistance.getInstance();
    }

    public AdjMatrixGraph(int size) {
        this(size, EuclideanDistance.getInstance());
    }

    public AdjMatrixGraph(int size, DistanceMeasure dm) {
        this.dm = dm;
        ensureCapacity(size);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final void ensureCapacity(int size) {
        if (size <= 0) {
            throw new RuntimeException("graph can't be initialized with " + size + " size!");
        }
        this.size = size;
        this.nodes = new AdjMatrixNode[size];
        nodeCounter = 0;
        edgeCounter = 0;
        adjMatrix = new AdjMatrixEdge[size][size];
        idToIndex = new HashMap<>();
        idToNode = new HashMap<>();
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
        edgeCounter++;
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
        edgeCounter--;
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
        return new AdjMatrixNodeIterable(nodes, nodeCounter);
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
        return edgeCounter;
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
        GraphFactory f = getFactory();
        for (int i = 0; i < nodeCounter; i++) {
            for (int j = 0; j < k; j++) {
                double distance = dm.measure(nodes[i].getInstance(), nodes[neighbors[i][j]].getInstance());
                if (distance < EPS) {
                    distance = EPS;
                }
                addEdge(f.newEdge(nodes[i], nodes[neighbors[i][j]], 1, 1 / distance, false)); //max val
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
