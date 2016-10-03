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
package org.clueminer.graph.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Lookup;

/**
 * Graph interface.
 * @param <E> base data type
 */
public interface Graph<E extends Instance> {

    /**
     * A unique identifier of graph storage implementation
     *
     * @return
     */
    String getName();

    /**
     * Adds an edge to this graph.
     *
     * @param edge the edge to add
     * @return true if the edge has been added, false if it already exists
     */
    boolean addEdge(Edge edge);

    /**
     * Adds a node to this graph.
     *
     * @param node the node to add
     * @return true if the node has been added, false if it already exists
     */
    boolean addNode(Node<E> node);

    /**
     * Adds all edges in the collection to this graph.
     *
     * @param edges the edge collection
     * @return true if at least one edge has been added, false otherwise
     */
    boolean addAllEdges(Collection<? extends Edge> edges);

    /**
     * Adds all nodes in the collection to this graph.
     *
     * @param nodes the node collection
     * @return true if at least one node has been added, false otherwise
     */
    boolean addAllNodes(Collection<? extends Node<E>> nodes);

    /**
     * Removes an edge from this graph.
     *
     * @param edge the edge to remove
     * @return true if the edge was removed, false if it didn't exist
     */
    boolean removeEdge(Edge edge);

    /**
     * Removes a node from this graph.
     *
     * @param node the node to remove
     * @return true if the node was removed, false if it didn't exist
     */
    boolean removeNode(Node<E> node);

    /**
     * Removes all edges in the collection from this graph.
     *
     * @param edges the edge collection
     * @return true if at least one edge has been removed, false otherwise
     */
    boolean removeAllEdges(Collection<? extends Edge> edges);

    /**
     * Removes all nodes in the collection from this graph.
     *
     * @param nodes the node collection
     * @return true if at least one node has been removed, false otherwise
     */
    boolean removeAllNodes(Collection<? extends Node<E>> nodes);

    /**
     * Returns true if <em>node</em> is contained in this graph.
     *
     * @param node the node to test
     * @return true if this graph contains <em>node</em>, false otherwise
     */
    boolean contains(Node<E> node);

    /**
     * Returns true if <em>edge</em> is contained in this graph.
     *
     * @param edge the edge to test
     * @return true if this graph contains <em>edge</em>, false otherwise
     */
    boolean contains(Edge edge);

    /**
     * Gets a node given its identifier.
     *
     * @param id the node id
     * @return the node, or null if not found
     */
    Node getNode(long id);

    /**
     * Gets an edge by its identifier.
     *
     * @param id the edge id
     * @return the edge, or null if not found
     */
    Edge getEdge(long id);

    /**
     * Gets the edge adjacent to node1 and node2.
     *
     * @param node1 the first node
     * @param node2 the second node
     * @return the adjacent edge, or null if not found
     */
    Edge getEdge(Node<E> node1, Node<E> node2);

    /**
     * Gets the edge adjacent to node1 and node2 and from the given type.
     *
     * @param node1 the first node
     * @param node2 the second node
     * @param type the edge type
     * @return the adjacent edge, or null if not found
     */
    Edge getEdge(Node<E> node1, Node<E> node2, int type);

    /**
     * Gets all the nodes in the graph.
     *
     * @return a node iterable over all nodes
     */
    NodeIterable getNodes();

    /**
     * Gets all the edges in the graph.
     *
     * @return an edge iterable over all edges
     */
    EdgeIterable getEdges();

    /**
     * Gets all the self-loop edges in the graph.
     *
     * @return an edge iterable over all self-loops
     */
    EdgeIterable getSelfLoops();

    /**
     * Gets all neighbors of a given node.
     *
     * @param node the node to get neighbors
     * @return a node iterable over the neighbors
     */
    NodeIterable getNeighbors(Node<E> node);

    /**
     * Gets all neighbors of a given node connected through the given edge type.
     *
     * @param node the node to get neighbors
     * @param type the edge type
     * @return a node iterable over the neigbors
     */
    NodeIterable getNeighbors(Node<E> node, int type);

    /**
     * Gets all edges incident to a given node.
     *
     * @param node the node to get edges from
     * @return an edge iterable of all edges connected to the node
     */
    EdgeIterable getEdges(Node<E> node);

    /**
     * Gets all edges incident to a given node with the given edge type.
     *
     * @param node the node to get edges from
     * @param type the edge type
     * @return an edge iterable of the edges connected to the node
     */
    EdgeIterable getEdges(Node<E> node, int type);

    /**
     * Gets the number of nodes in the graph.
     *
     * @return the node count
     */
    int getNodeCount();

    /**
     * Gets the number of edges in the graph.
     *
     * @return the edge count
     */
    int getEdgeCount();

    /**
     * Gets the number of edges of the given type in the graph.
     *
     * @param type the edge type
     * @return the edge count for the given type
     */
    int getEdgeCount(int type);

    /**
     * Gets the node at the opposite end of the given edge.
     *
     * @param node the node to get the opposite
     * @param edge the edge connected to both nodes
     * @return the opposite node
     */
    Node getOpposite(Node<E> node, Edge edge);

    /**
     * Gets the node degree.
     *
     * @param node the node
     * @return the degree
     */
    int getDegree(Node<E> node);

    /**
     * Returns true if the given edge is a self-loop.
     *
     * @param edge the edge to test
     * @return true if self-loop, false otherwise
     */
    boolean isSelfLoop(Edge edge);

    /**
     * Returns true if the given edge is directed.
     *
     * @param edge the edge to test
     * @return true if directed, false otherwise
     */
    boolean isDirected(Edge edge);

    /**
     * Returns true if node1 and node2 are adjacent.
     *
     * @param node1 the first node
     * @param node2 the second node
     * @return true if node1 is adjacent to node2, false otherwise
     */
    boolean isAdjacent(Node<E> node1, Node<E> node2);

    /**
     * Returns true if node1 and node2 are adjacent with an edge of the given
     * type.
     *
     * @param node1 the first node
     * @param node2 the second node
     * @param type the edge type
     * @return true if node1 and node2 are adjacent with an edge og the given
     * type, false otherwise
     */
    boolean isAdjacent(Node<E> node1, Node<E> node2, int type);

    /**
     * Returns true if edge1 and edge2 are incident.
     *
     * @param edge1 the first edge
     * @param edge2 the second edge
     * @return true if edge1 is incident to edge2, false otherwise
     */
    boolean isIncident(Edge edge1, Edge edge2);

    /**
     * Returns true if the node and the edge are incident.
     *
     * @param node the node
     * @param edge the edge
     * @return true if the node and edge are incident, false otherwise
     */
    boolean isIncident(Node<E> node, Edge edge);

    /**
     * Clears the edges incident to the given node.
     *
     * @param node the node to clear edges from
     */
    void clearEdges(Node<E> node);

    /**
     * Clears the edges of the given type incident to the given node.
     *
     * @param node the node to clear edges from
     * @param type the edge type
     */
    void clearEdges(Node<E> node, int type);

    /**
     * Clears all edges and all nodes in the graph
     */
    void clear();

    /**
     * Clears all edges in the graph
     */
    void clearEdges();

    /**
     * Returns true if this graph is directed.
     *
     * @return true if directed, false otherwise
     */
    boolean isDirected();

    /**
     * Returns true if this graph is undirected.
     *
     * @return true if undirected, false otherwise
     */
    boolean isUndirected();

    /**
     * Returns true if this graph is mixed (both directed and undirected edges).
     *
     * @return true if mixed, false otherwise
     */
    boolean isMixed();

    /**
     * Returns factory for this graph
     *
     * @return graph factory
     */
    GraphBuilder getFactory();

    /**
     * Create edges between neighbors according to neighbor array
     *
     * @param nearests array with nearest neighbors
     * @param k number of neighbors for each node
     * @return true if all nodes were added, false otherwise
     */
    boolean addEdgesFromNeigborArray(int[][] nearests, int k);

    /**
     * Returns index of node in this graph
     *
     * @param node the node to get index from
     * @return node index
     */
    int getIndex(Node node);

    /**
     * Ensure graph-store capacity
     *
     * @param size
     */
    void ensureCapacity(int size);

    /**
     * Export graph in the metis format
     *
     * @param weighted whether the edge weights should be exported
     * @return
     */
    String metisExport(boolean weighted);

    /**
     * Export a (hyper)graph into hmetis format. Each line is a hyperedge
     *
     * @param target
     * @param weighted
     * @throws java.io.FileNotFoundException
     */
    void hMetisExport(File target, boolean weighted) throws FileNotFoundException;

    /**
     * Lookup is used for retrieving objects associated with this graph
     * that does not necessarily has to be of pre-defined type (defined in this
     * API)
     *
     * @return lookup object
     */
    Lookup getLookup();

    /**
     * Add object to lookup
     *
     * @param instance
     */
    void lookupAdd(Object instance);

    /**
     * Removes object from lookup
     *
     * @param instance
     */
    void lookupRemove(Object instance);

    /**
     * Whether graph allows having same objects in different graphs.
     *
     * @return
     */
    boolean suppportReferences();

}
