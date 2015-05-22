package org.clueminer.graph.adjacencyList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 * This implementation is problematic for algorithms which create multiple
 * copies of the same graph, but doesn't want to copy edges. Currently list of
 * edges is kept in {@link AdjListNode}.
 *
 * @author Hamster
 */
//@ServiceProvider(service = Graph.class)
public class AdjListGraph implements Graph {

    private static final String name = "Adj List Graph";

    private final HashMap<Long, Node> nodes;
    private final HashMap<Long, Edge> edges;
    //mapping to Dataset's Instance index
    private final HashMap<Long, Integer> idToIndex;
    private final double EPS = 1e-6;
    private DistanceMeasure dm;

    public AdjListGraph() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        dm = EuclideanDistance.getInstance();
        idToIndex = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("Edges:");
        for (Map.Entry<Long, Edge> entrySet : edges.entrySet()) {
            Edge edge = entrySet.getValue();
            System.out.println(edge);
        }
        System.out.println("---------------------");
        System.out.println("Nodes:");
        for (Map.Entry<Long, Node> entrySet : nodes.entrySet()) {
            Node node = entrySet.getValue();
            System.out.println(node);
        }
        System.out.println("---------------------");
    }

    @Override
    public boolean addEdge(Edge edge) {
        if (edges.containsKey(edge.getId())) {
            return false;
        }
        if (!nodes.containsKey(edge.getSource().getId()) || !nodes.containsKey(edge.getTarget().getId())) {
            throw new IllegalArgumentException("Source or target node does not exist");
        }
        edges.put(edge.getId(), (AdjListEdge) edge);
        AdjListNode source = (AdjListNode) edge.getSource();
        AdjListNode target = (AdjListNode) edge.getTarget();
        source.addEdge(edge);
        target.addEdge(edge);
        return true;
    }

    @Override
    public boolean addNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            return false;
        }
        idToIndex.put(node.getId(), nodes.size());
        nodes.put(node.getId(), (AdjListNode) node);
        return true;
    }

    @Override
    public boolean addAllEdges(Collection<? extends Edge> edges) {
        boolean added = false;
        for (Edge edge : edges) {
            if (addEdge(edge)) {
                added = true;
            }
        }
        return added;
    }

    @Override
    public boolean addAllNodes(Collection<? extends Node> nodes) {
        boolean added = false;
        for (Node node : nodes) {
            if (addNode(node)) {
                added = true;
            }
        }
        return added;
    }

    @Override
    public boolean removeEdge(Edge edge) {
        if (edges.remove(edge.getId()) == null) {
            return false;
        }
        ((AdjListNode) edge.getSource()).removeEdge(edge);
        ((AdjListNode) edge.getTarget()).removeEdge(edge);
        return true;
    }

    @Override
    public boolean removeNode(Node node) {
        if (nodes.remove(node.getId()) == null) {
            return false;
        }
        for (Edge it : ((AdjListNode) node).getEdges()) {
            AdjListEdge edge = (AdjListEdge) it;
            if (edge.getSource() == node) {
                ((AdjListNode) edge.getTarget()).removeEdge(it);
            } else {
                ((AdjListNode) edge.getSource()).removeEdge(it);
            }
            edges.remove(it.getId());
            idToIndex.remove(it.getId());
        }
        return true;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Edge> edges) {
        boolean removed = false;
        for (Edge edge : edges) {
            if (removeEdge(edge)) {
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean removeAllNodes(Collection<? extends Node> nodes) {
        boolean removed = false;
        for (Node node : nodes) {
            if (removeNode(node)) {
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean contains(Node node) {
        return nodes.containsKey(node.getId());
    }

    @Override
    public boolean contains(Edge edge) {
        return edges.containsKey(edge.getId());
    }

    @Override
    public Node getNode(long id) {
        return nodes.get(id);
    }

    @Override
    public Edge getEdge(long id) {
        return edges.get(id);
    }

    @Override
    public Edge getEdge(Node node1, Node node2) {
        return ((AdjListNode) node1).getEdge(node2);
    }

    @Override
    public Edge getEdge(Node node1, Node node2, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeIterable getNodes() {
        return new AdjListNodeIterable(nodes);
    }

    @Override
    public EdgeIterable getEdges() {
        return new AdjListEdgeIterable(edges);
    }

    @Override
    public EdgeIterable getSelfLoops() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeIterable getNeighbors(Node node) {
        return ((AdjListNode) node).getNeighbors();
    }

    @Override
    public NodeIterable getNeighbors(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EdgeIterable getEdges(Node node) {
        return ((AdjListNode) node).getEdges();
    }

    @Override
    public EdgeIterable getEdges(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
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
        return ((AdjListNode) node).getDegree();
    }

    @Override
    public boolean isSelfLoop(Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDirected(Edge edge) {
        return edge.isDirected();
    }

    @Override
    public boolean isAdjacent(Node node1, Node node2) {
        return ((AdjListNode) node1).isAdjacent(node2);
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
        return edge.getSource() == node || edge.getTarget() == node;
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
        nodes.clear();
        edges.clear();
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

    @Override
    public GraphFactory getFactory() {
        return AdjListFactory.getInstance();
    }

    @Override
    public boolean addEdgesFromNeigborArray(int[][] neighbors, int k) {
        if (k > getNodeCount()) {
            return false;
        }
        GraphFactory f = AdjListFactory.getInstance();
        //go though all nodes in the graph (dataset)
        Instance curr;
        Node other;
        for (Node node : getNodes()) {
            //for each Instance(Node) find its k-neighbours
            curr = node.getInstance();
            for (int j = 0; j < k; j++) {
                other = getNode(indexToId(neighbors[getIndex(node)][j]));
                double distance = dm.measure(curr, other.getInstance());
                if (distance < EPS) {
                    distance = EPS;
                }
                addEdge(f.newEdge(node, other, 1, 1 / distance, false)); //max val
            }
        }
        return true;
    }

    /**
     * TODO: this is really ugly and inefficient (use Guava or some other
     * collection)
     *
     * @param <T>
     * @param <E>
     * @param map
     * @param value
     * @return
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public long indexToId(int index) {
        return getKeyByValue(idToIndex, index);
    }

    @Override
    public int getIndex(Node node) {
        return idToIndex.get(node.getId());
    }

    @Override
    public void ensureCapacity(int size) {
        //nothing to do
    }

}
