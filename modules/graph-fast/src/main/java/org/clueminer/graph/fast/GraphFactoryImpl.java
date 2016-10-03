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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeType;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class GraphFactoryImpl<E extends Instance> implements GraphBuilder<E> {

    protected final AtomicLong NODE_IDS = new AtomicLong();
    protected final AtomicLong EDGE_IDS = new AtomicLong();
    protected final FastGraph store;
    private static GraphFactoryImpl instance;

    public GraphFactoryImpl(FastGraph graph) {
        this.store = graph;
    }

    public GraphFactoryImpl() {
        store = new FastGraph();
    }

    public static GraphFactoryImpl getInstance() {
        if (instance == null) {
            instance = new GraphFactoryImpl();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "graph-fast";
    }

    @Override
    public Edge newEdge(Node source, Node target) {
        return newEdge(source, target, EdgeType.NONE.getValue(), 1.0, false);
    }

    @Override
    public Edge newEdge(Node source, Node target, boolean directed) {
        EdgeType dir = directed ? EdgeType.FORWARD : EdgeType.NONE;
        return newEdge(source, target, dir.getValue(), 1.0, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, boolean directed) {
        return newEdge(source, target, type, 1.0, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, double weight, boolean directed) {
        if (!store.allowParallelEdges()) {
            //return existing edge, in case that parallel edges aren't allowed
            Edge edge = store.getEdge(source, target);
            if (edge == null) {
                edge = store.getEdge(target, source);
            }
            if (edge != null) {
                return edge;
            }
        }
        EdgeType dir = directed ? EdgeType.FORWARD : EdgeType.NONE;
        return new EdgeImpl(EDGE_IDS.getAndIncrement(), store, source, target, dir.getValue(), weight);
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        long idx = (long) id;
        EdgeType dir = directed ? EdgeType.FORWARD : EdgeType.NONE;
        return new EdgeImpl(idx, store, source, target, dir.getValue(), weight);
    }

    @Override
    public Node newNode() {
        return new NodeImpl(NODE_IDS.getAndIncrement(), store);
    }

    @Override
    public Node newNode(Object label) {
        return new NodeImpl(NODE_IDS.getAndIncrement(), store, label);
    }

    @Override
    public Node newNode(Instance i) {
        Node n = new NodeImpl(NODE_IDS.getAndIncrement(), store);
        n.setInstance(i);
        return n;
    }

    @Override
    public ArrayList<Node<E>> createNodesFromInput(Dataset<E> input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
