package org.clueminer.chameleon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JComponent;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.math.Matrix;
import org.clueminer.partitioning.api.Bisection;
import org.openide.util.Exceptions;

/**
 * A cluster backed by a graph structure for computing cluster properties
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class GraphCluster<E extends Instance> implements Cluster<E>, Set<E> {

    private static final long serialVersionUID = 5504099696730158249L;

    /**
     * Internal interconnectivity of the cluster (sum of cut edges)
     */
    private double IIC = -1;

    /**
     * Internal closeness of the cluster (average of cut edges)
     */
    private double ICL = -1;

    /**
     * Average closeness of the cluster (average of all edges)
     */
    private double ACL = -1;

    private Bisection bisection;

    private GraphCluster<E> parent;

    public LinkedList<GraphCluster<E>> offsprings;

    private int edgeCount;

    /**
     * Nodes belonging to the cluster
     */
    private final LinkedList<Node<E>> graphNodes;

    private final Graph parentGraph;

    private int id;

    private Color color;

    public GraphCluster(LinkedList<Node<E>> n, Graph g, int index, Bisection bisection) {
        parentGraph = g;
        graphNodes = n;
        edgeCount = -1;
        this.id = index;
        this.bisection = bisection;
    }

    /**
     * Bisects the graph and computes IIC and ICL from the bisection
     *
     */
    protected void computeBisectionProperties() {
        Graph graph = buildGraphFromCluster(graphNodes, parentGraph);
        //If bisection cannot be done, set values to 1
        if (graph.getNodeCount() == 1) {
            ICL = IIC = 1;
            return;
        }
        ArrayList<LinkedList<Node>> result = bisection.bisect(graph);
        IIC = ICL = 0;
        int counter = 0;
        for (Node node1 : result.get(0)) {
            for (Node node2 : result.get(1)) {
                if (graph.isAdjacent(node1, node2)) {
                    IIC += graph.getEdge(node1, node2).getWeight();
                    counter++;
                }
            }
        }
        if (counter > 0) {
            ICL = IIC / counter;
        } else {
            ICL = Double.MAX_VALUE;
        }
    }

    /**
     * Computes average weight of all edges in the graph
     */
    protected void computeAverageCloseness() {
        Graph graph = buildGraphFromCluster(graphNodes, parentGraph);

        double sum = 0;
        if (edgeCount == 0) {
            ACL = 0;
            return;
        }
        for (Edge e : graph.getEdges()) {
            sum += e.getWeight();
        }
        ACL = sum / edgeCount;
    }

    /**
     * Builds graph from list of nodes and parent graph
     *
     * @param n List of nodes
     * @param g Parent graph
     * @return Graph representing this cluster
     */
    protected Graph buildGraphFromCluster(LinkedList<Node<E>> n, Graph g) {
        ArrayList<Node<E>> nodes = new ArrayList<>(n);
        Graph graph = null;
        try {
            graph = g.getClass().newInstance();
            graph.ensureCapacity(nodes.size());
            for (Node node : nodes) {
                graph.addNode(node);
            }
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    if (g.isAdjacent(nodes.get(i), nodes.get(j))) {
                        graph.addEdge(g.getEdge(nodes.get(i), nodes.get(j)));
                    }
                }
            }
            edgeCount = graph.getEdgeCount();
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return graph;
    }

    /**
     *
     * @return Internal interconnectivity computed by bisection
     */
    public double getIIC() {
        if (IIC == -1) {
            computeBisectionProperties();
        }
        return IIC;
    }

    /**
     *
     * @return Internal closeness computed by bisection
     */
    public double getICL() {
        if (ICL == -1) {
            computeBisectionProperties();
        }
        return ICL;
    }

    /**
     *
     * @return Average internal closeness
     */
    public double getACL() {
        if (ACL == -1) {
            computeAverageCloseness();
        }
        return ACL;
    }

    public void setACL(double ACL) {
        this.ACL = ACL;
    }

    /**
     * Number of edges in the cluster
     *
     * @return
     */
    public int getEdgeCount() {
        if (edgeCount == -1) {
            /*Edgecount is usually needed together with ACL. In order to build graph
             only once, we compute the ACL value (during witch graph is built
             and edge count determined) here*/
            computeAverageCloseness();
        }
        return edgeCount;
    }

    public void setEdgeCount(int count) {
        edgeCount = count;
    }

    public LinkedList<Node<E>> getNodes() {
        return graphNodes;
    }

    public int getNodeCount() {
        return graphNodes.size();
    }

    public Graph getGraph() {
        return parentGraph;
    }

    @Override
    public int getClusterId() {
        return id;
    }

    @Override
    public GraphCluster<E> getParent() {
        return parent;
    }

    public void setParent(GraphCluster<E> parent) {
        this.parent = parent;
    }

    @Override
    public void setClusterId(int id) {
        this.id = id;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public E getCentroid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int countMutualElements(Cluster<E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(int origId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "cluster " + id;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SortedSet<Object> getClasses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(E i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Dataset<? extends E> d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E instance(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasIndex(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E getRandom(Random rand) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return graphNodes.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean hasParent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int attributeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int classIndex(Object clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object classValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changedClass(Object orig, Object current, Object source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute[] copyAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute[] attributeByRole(AttributeRole role) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Attribute> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute getAttribute(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAttribute(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute getAttribute(String attributeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int instanceIdx, int attributeIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int instanceIdx, int attrIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E set(int instanceIdx, E inst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(int index, Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InstanceBuilder<E> builder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeBuilder attributeBuilder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<? extends E> copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<? extends E> duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] arrayCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JComponent getPlotter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ensureCapacity(int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addChild(String key, Dataset<E> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<E> getChild(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix asMatrix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double min() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double max() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetStats() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends Number> attrCollection(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParent(Dataset<E> parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GraphCluster ");
        sb.append(getName());
        sb.append(" (").append(size()).append(") ");
        sb.append(" [ ");
        Node<E> elem;
        for (int i = 0; i < this.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            elem = this.graphNodes.get(i);
            sb.append(elem.getId());
        }
        sb.append(" ]");
        return sb.toString();
    }

}
