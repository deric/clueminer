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
package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 * @param <E>
 */
@ServiceProvider(service = GraphBuilder.class)
public class AdjListFactory<E extends Instance> implements GraphBuilder<E> {

    private static AdjListFactory instance;

    private static long nodeIdCounter = 0;
    private static long edgeIdCounter = 0;

    public static AdjListFactory getInstance() {
        if (instance == null) {
            instance = new AdjListFactory();
        }
        return instance;
    }

    public AdjListFactory() {
    }

    @Override
    public String getName() {
        return "AdjListFactory";
    }

    @Override
    public Edge newEdge(Node source, Node target) {
        return new AdjListEdge(edgeIdCounter++, source, target);
    }

    @Override
    public Edge newEdge(Node source, Node target, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed);
    }

    @Override
    public Edge newEdge(Node source, Node target, int type, double weight, boolean directed) {
        return new AdjListEdge(edgeIdCounter++, source, target, directed, weight);
    }

    @Override
    public Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node newNode() {
        return new AdjListNode(nodeIdCounter++);
    }

    @Override
    public Node newNode(Object label) {
        return new AdjListNode(nodeIdCounter++, label);
    }

    @Override
    public ArrayList<Node<E>> createNodesFromInput(Dataset<E> input) {
        nodeIdCounter = 0;
        edgeIdCounter = 0;
        ArrayList<Node<E>> nodes = new ArrayList<>(input.size());
        for (Instance inputInstance : input) {
            Node node = this.newNode();
            node.setInstance(inputInstance);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public Node<E> newNode(E instance) {
        Node node = this.newNode();
        node.setInstance(instance);
        return node;
    }

    protected static long getNodeCount() {
        return nodeIdCounter;
    }

    protected static long getEdgeCount() {
        return edgeIdCounter;
    }

    @Override
    public Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph) {
        nodeIdCounter = 0;
        edgeIdCounter = 0;
        Long[] mapping = new Long[input.size()];
        for (E inst : input) {
            Node node = this.newNode();
            mapping[inst.getIndex()] = node.getId();
            node.setInstance(inst);
            graph.addNode(node);
        }
        return mapping;
    }

}
