package org.clueminer.graph.adjacencyList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphFactory;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Hamster
 */
@ServiceProvider(service = Graph.class)
public class AdjListGraph implements Graph {

    private static final String name = "Adjacency list graph";

    private final HashMap<Long, Node> nodes;
    private final HashMap<Long, Edge> edges;
    //mapping to Dataset's Instance index
    private final HashMap<Long, Integer> idToIndex;
    private final double EPS = 1e-6;
    private final Distance dm;

    //Lookup
    private final transient InstanceContent instanceContent;
    private final transient AbstractLookup lookup;

    private final HashMap<Node, Set<Neighbor>> adjList;

    public AdjListGraph() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        dm = EuclideanDistance.getInstance();
        idToIndex = new HashMap<>();
        adjList = new HashMap<>();
        //lookup
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
    }

    public AdjListGraph(int size) {
        this();
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
        AdjListNode source = (AdjListNode) edge.getSource();
        AdjListNode target = (AdjListNode) edge.getTarget();
        if (adjList.get(source).add(new Neighbor(edge, target))
                && adjList.get(target).add(new Neighbor(edge, source))) {
            edges.put(edge.getId(), (AdjListEdge) edge);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean addNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            return false;
        }
        idToIndex.put(node.getId(), nodes.size());
        adjList.put(node, new TreeSet<Neighbor>());
        nodes.put(node.getId(), (AdjListNode) node);
        return true;
    }

    @Override
    public boolean addAllEdges(Collection<? extends Edge> edges) {
        boolean success = true;
        for (Edge edge : edges) {
            if (!addEdge(edge)) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean addAllNodes(Collection<? extends Node> nodes) {
        boolean success = true;
        for (Node node : nodes) {
            if (!addNode(node)) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean removeEdge(Edge edge) {
        if (edges.remove(edge.getId()) == null) {
            return false;
        }
        AdjListNode source = (AdjListNode) edge.getSource();
        AdjListNode target = (AdjListNode) edge.getTarget();
        Neighbor n = new Neighbor(edge, target);
        adjList.get(source).remove(n);
        n = new Neighbor(edge, source);
        adjList.get(target).remove(n);
        return true;
    }

    @Override
    public boolean removeNode(Node node) {
        if (nodes.remove(node.getId()) == null) {
            return false;
        }
        Neighbor self = new Neighbor(null, node);
        for (Neighbor neighbor : adjList.get(node)) {
            edges.remove(neighbor.edge.getId());
            adjList.get(neighbor.node).remove(self);
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
        for (Neighbor n : adjList.get(node1)) {
            if (n.node == node2) {
                return n.edge;
            }
        }
        return null;
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
        LinkedList<Node> neighbors = new LinkedList<>();
        for (Neighbor n : adjList.get(node)) {
            neighbors.add(n.node);
        }
        return new AdjListNodeIterable(neighbors);
    }

    @Override
    public NodeIterable getNeighbors(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EdgeIterable getEdges(Node node) {
        LinkedList<Edge> adjEdges = new LinkedList<>();
        for (Neighbor n : adjList.get(node)) {
            adjEdges.add(n.edge);
        }
        return new AdjListEdgeIterable(adjEdges);
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
        return adjList.get(node).size();
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
        Neighbor n = new Neighbor(null, node2);
        return adjList.get(node1).contains(n);
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

    /**
     * Export graph to METIS format. ID of node is given by line number. Node
     * numbering start from 1
     *
     * {node count} {edges count}
     *
     * {neighbour1} {neighbour2} {neighbour3}
     *
     * @param weighted
     * @return
     */
    @Override
    public String metisExport(boolean weighted) {
        Node[] nodeMapping = new Node[getNodeCount()];
        for (Node node : getNodes()) {
            nodeMapping[idToIndex.get(node.getId())] = node;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getNodeCount()).append(" ").append(getEdgeCount()).append("\n");
        for (int i = 0; i < getNodeCount(); i++) {
            String space = "";
            for (Node neighbor : getNeighbors(nodeMapping[i])) {
                sb.append(space).append(idToIndex.get(neighbor.getId()) + 1);
                space = " ";
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     *
     * {hyperedge cnt} {node cnt}
     *
     * //list of hyperedges goes here
     *
     * @param weighted
     */
    @Override
    public void hMetisExport(File target, boolean weighted) throws FileNotFoundException {
        StringBuilder sb;
        try (PrintWriter writer = new PrintWriter(target, "UTF-8")) {
            Node[] nodeMapping = new Node[getNodeCount()];
            for (Node node : getNodes()) {
                nodeMapping[idToIndex.get(node.getId())] = node;
            }
            sb = new StringBuilder();
            //same number of nodes as hyperedges - a hyperedge is formed by node's neighbourhood
            sb.append(getNodeCount()).append(" ").append(getNodeCount()).append("\n");
            for (int i = 0; i < getNodeCount(); i++) {
                String space = "";
                for (Node neighbor : getNeighbors(nodeMapping[i])) {
                    sb.append(space).append(idToIndex.get(neighbor.getId()) + 1);
                    space = " ";
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void lookupAdd(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void lookupRemove(Object instance) {
        instanceContent.remove(instance);
    }

    private class Neighbor implements Comparable<Neighbor> {

        Edge edge;
        Node node;

        public Neighbor(Edge e, Node n) {
            edge = e;
            node = n;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Neighbor)) {
                return false;
            }
            Neighbor other = (Neighbor) o;
            return node.getId() == other.node.getId();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + Objects.hashCode(this.node);
            return hash;
        }

        @Override
        public int compareTo(Neighbor o) {
            return (int) (node.getId() - o.node.getId());
        }

    }

}
