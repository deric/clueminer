/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.graph.fast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Graph.class)
public class FastGraph<E extends Instance> implements Graph<E> {

    private static final String NAME = "Fast Graph";

    //Lookup
    private final transient InstanceContent instanceContent;
    private final transient AbstractLookup lookup;

    protected final NodeStore nodeStore;
    protected final EdgeStore edgeStore;

    /**
     * Whether multiple edges can exist between two vertices
     */
    private boolean parallelEdges = false;

    private Distance dm;
    private final double EPS = 1e-6;

    public FastGraph() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        nodeStore = new NodeStore();
        edgeStore = new EdgeStore();
    }

    public FastGraph(boolean allowRef) {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        nodeStore = new NodeStore(allowRef);
        edgeStore = new EdgeStore(allowRef);
    }

    public FastGraph(int size) {
        this();
        ensureCapacity(size);
        dm = DistanceFactory.getInstance().getProvider("Euclidean");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean addEdge(Edge edge) {
        return edgeStore.add(edge);
    }

    @Override
    public boolean addNode(Node node) {
        return nodeStore.add(node);
    }

    @Override
    public boolean addAllEdges(Collection<? extends Edge> edges) {
        return edgeStore.addAll(edges);
    }

    @Override
    public boolean removeEdge(Edge edge) {
        return edgeStore.remove(edge);
    }

    @Override
    public boolean removeNode(Node node) {
        return nodeStore.remove(node);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Edge> edges) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Node node) {
        return nodeStore.contains(node);
    }

    @Override
    public boolean contains(Edge edge) {
        return edgeStore.contains(edge);
    }

    @Override
    public Node getNode(long id) {
        return nodeStore.get(id);
    }

    @Override
    public Edge getEdge(long id) {
        return edgeStore.get(id);
    }

    @Override
    public Edge getEdge(Node node1, Node node2) {
        return edgeStore.get(node1, node2);
    }

    @Override
    public Edge getEdge(Node node1, Node node2, int type) {
        return edgeStore.get(node1, node2);
    }

    @Override
    public NodeIterable getNodes() {
        return nodeStore;
    }

    @Override
    public EdgeIterable getEdges() {
        return edgeStore;
    }

    @Override
    public EdgeIterable getSelfLoops() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeIterable getNeighbors(Node node) {
        return new NodeIterableWrapper(edgeStore.neighborIterator(node));
    }

    @Override
    public NodeIterable getNeighbors(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EdgeIterable getEdges(Node node) {
        return new EdgeIterableWrapper(edgeStore.edgeIterator(node));
    }

    @Override
    public EdgeIterable getEdges(Node node, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNodeCount() {
        return nodeStore.size();
    }

    @Override
    public int getEdgeCount() {
        return edgeStore.size();
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
        nodeStore.checkNonNullNodeObject(node);
        return ((NodeImpl) node).getDegree();
    }

    @Override
    public boolean isSelfLoop(Edge edge) {
        return ((EdgeImpl) edge).isSelfLoop();
    }

    @Override
    public boolean isDirected(Edge edge) {
        return edge.isDirected();
    }

    @Override
    public boolean isAdjacent(Node node1, Node node2) {
        return edgeStore.isAdjacent(node1, node2);
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
        edgeStore.clear();
        nodeStore.clear();
    }

    @Override
    public void clearEdges() {
        edgeStore.clear();
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
    public GraphBuilder getFactory() {
        return GraphFactoryImpl.getInstance();
    }

    @Override
    public boolean addEdgesFromNeigborArray(int[][] neighbors, int k) {
        if (k > nodeStore.size()) {
            return false;
        }
        GraphBuilder f = getFactory();
        for (int i = 0; i < nodeStore.size(); i++) {
            for (int j = 0; j < k; j++) {
                double distance = dm.measure(nodeStore.get(i).getInstance(), nodeStore.get(neighbors[i][j]).getInstance());
                if (distance < EPS) {
                    distance = EPS;
                }
                addEdge(f.newEdge(nodeStore.get(i), nodeStore.get(neighbors[i][j]), 1, 1 / distance, false)); //max val
            }
        }
        return true;
    }

    @Override
    public int getIndex(Node node) {
        NodeImpl n = (NodeImpl) node;
        return n.getStoreId();
    }

    @Override
    public void ensureCapacity(int size) {
        //TODO: initialize store?
    }

    @Override
    public String metisExport(boolean weighted) {
        StringBuilder sb = new StringBuilder();
        sb.append(getNodeCount()).append(" ").append(getEdgeCount()).append("\n");
        //for (int i = 0; i < getNodeCount(); i++) {
        for (Node node : getNodes()) {
            String space = "";
            for (Node neighbor : getNeighbors(node)) {
                NodeImpl nn = (NodeImpl) neighbor;
                sb.append(space).append(nn.storeId + 1);
                space = " ";
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public boolean allowParallelEdges() {
        return parallelEdges;
    }

    public void setAllowParallelEdges(boolean b) {
        this.parallelEdges = b;
    }

    @Override
    public void lookupAdd(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void lookupRemove(Object instance) {
        instanceContent.remove(instance);
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
            sb = new StringBuilder();
            //same number of nodes as hyperedges - a hyperedge is formed by node's neighbourhood
            sb.append(getNodeCount()).append(" ").append(getNodeCount()).append("\n");
            String space = " ";
            //for (int i = 0; i < getNodeCount(); i++) {
            for (Node node : getNodes()) {
                //append self
                NodeImpl n = (NodeImpl) node;
                sb.append(n.storeId + 1);
                for (Node neighbor : getNeighbors(node)) {
                    NodeImpl nn = (NodeImpl) neighbor;
                    sb.append(space).append(nn.storeId + 1);
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean addAllNodes(Collection<? extends Node<E>> nodes) {
        boolean success = true;
        for (Node node : nodes) {
            if (!addNode(node)) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean removeAllNodes(Collection<? extends Node<E>> nodes) {
        boolean removed = false;
        for (Node node : nodes) {
            if (removeNode(node)) {
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean suppportReferences() {
        return true;
    }

    protected class NodeIterableWrapper implements NodeIterable {

        protected final Iterator<Node> iterator;

        public NodeIterableWrapper(Iterator<Node> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<Node> iterator() {
            return iterator;
        }

        @Override
        public Node[] toArray() {
            List<Node> list = new ArrayList<>();
            for (; iterator.hasNext();) {
                list.add(iterator.next());
            }
            return list.toArray(new Node[0]);
        }

        @Override
        public Collection<Node> toCollection() {
            List<Node> list = new ArrayList<>();
            for (; iterator.hasNext();) {
                list.add(iterator.next());
            }
            return list;
        }

        @Override
        public void doBreak() {

        }

        @Override
        public int size() {
            return 0;
        }
    }

    protected class EdgeIterableWrapper implements EdgeIterable {

        protected final Iterator<Edge> iterator;

        public EdgeIterableWrapper(Iterator<Edge> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<Edge> iterator() {
            return iterator;
        }

        @Override
        public Edge[] toArray() {
            List<Edge> list = new ArrayList<>();
            for (; iterator.hasNext();) {
                list.add(iterator.next());
            }
            return list.toArray(new Edge[0]);
        }

        @Override
        public Collection<Edge> toCollection() {
            List<Edge> list = new ArrayList<>();
            for (; iterator.hasNext();) {
                list.add(iterator.next());
            }
            return list;
        }

        @Override
        public void doBreak() {

        }

        @Override
        public int size() {
            return 0;
        }
    }

}
