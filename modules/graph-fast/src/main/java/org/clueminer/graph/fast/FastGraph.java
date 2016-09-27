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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author deric
 */
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

    public FastGraph() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        nodeStore = new NodeStore();
        edgeStore = new EdgeStore();
    }

    public FastGraph(int size) {
        this();
        ensureCapacity(size);
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
    public GraphBuilder getFactory() {
        return GraphFactoryImpl.getInstance();
    }

    @Override
    public boolean addEdgesFromNeigborArray(int[][] nearests, int k) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public void hMetisExport(File target, boolean weighted) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
