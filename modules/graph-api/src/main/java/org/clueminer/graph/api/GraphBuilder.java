/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.graph.api;

import java.util.ArrayList;
import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Factory for nodes and edges.
 * <p>
 * All new nodes and edges are created by this factory.
 * <p>
 * Both nodes and edges have unique identifiers. If not provided, a unique id
 * will be automatically assigned to the elements.
 *
 * @param <E>
 */
public interface GraphBuilder<E extends Instance> {

    /**
     *
     * @return name of the builder
     */
    String getName();

    /**
     * Creates and returns a directed edge between source and target.
     *
     * @param source the source node
     * @param target the target node
     * @return the new edge
     */
    Edge newEdge(Node source, Node target);

    /**
     * Creates and returns an edge between source and target.
     *
     * @param source   the source node
     * @param target   the target node
     * @param directed true if directed, false if undirected
     * @return the new edge
     */
    Edge newEdge(Node source, Node target, boolean directed);

    /**
     * Creates and returns an edge between source and target.
     *
     * @param source   the source node
     * @param target   the target node
     * @param type     the edge type
     * @param directed true if directed, false if undirected
     * @return the new edge
     */
    Edge newEdge(Node source, Node target, int type, boolean directed);

    /**
     * Creates and returns an edge between source and target.
     *
     * @param source   the source node
     * @param target   the target node
     * @param type     the edge type
     * @param weight   the edge weight
     * @param directed true if directed, false if undirected
     * @return the new edge
     */
    Edge newEdge(Node source, Node target, int type, double weight, boolean directed);

    /**
     * Creates and returns an edge between source and target.
     *
     * @param id       the edge id
     * @param source   the source node
     * @param target   the target node
     * @param type     the edge type
     * @param weight   the edge weight
     * @param directed true if directed, false if undirected
     * @return the new edge
     */
    Edge newEdge(Object id, Node source, Node target, int type, double weight, boolean directed);

    /**
     * Creates and returns a node.
     *
     * @return the new node
     */
    Node<E> newNode();

    /**
     * Creates and returns a node.
     *
     * @param label the node label
     * @return the new node
     */
    Node<E> newNode(Object label);

    /**
     * Creates and returns a node.
     *
     * @param i instance which the node represents
     * @return the new node
     */
    Node<E> newNode(E i);

    /**
     * Creates nodes from the dataset
     *
     * @param input input dataset
     * @return list of nodes
     */
    ArrayList<Node<E>> createNodesFromInput(Dataset<E> input);

    /**
     * Create nodes from dataset's instances and add them to the graph
     *
     * @param input source data
     * @param graph target graph
     * @return mapping from Dataset index -> Node ID
     */
    Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph);

    /**
     * Create nodes from dataset's instances and add them to the graph
     *
     * @param input source data
     * @param graph target graph
     * @param noise items marked as noise (to be excluded)
     * @return mapping from Dataset index -> Node ID
     */
    Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph, HashSet<Integer> noise);

}
