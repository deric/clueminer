/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.importer.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import org.clueminer.graph.api.EdgeType;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphBuilderFactory;
import org.clueminer.io.importer.api.EdgeDraft;
import org.clueminer.io.importer.api.NodeDraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 */
public class GraphDraft<E extends NodeDraft> extends DraftContainer<E> {

    private GraphBuilder factory;
    private static final Logger LOG = LoggerFactory.getLogger(GraphDraft.class);
    private EdgeType edgeDefault;
    private Object2ObjectMap<String, NodeDraft> nodeMap;
    private Set<EdgeDraft> edgeMap;

    public GraphDraft() {
        nodeMap = new Object2ObjectOpenHashMap<>();
        edgeMap = new HashSet<>();
        this.factory = GraphBuilderFactory.getInstance().getDefault();
        LOG.info("building graph with {}", factory.getName());
    }

    public NodeDraft newNodeDraft(String id) {
        NodeDraft node = new NodeDraftImpl(this);
        node.setId(id);
        //TODO add node to tmp graph
        return node;
    }

    public NodeDraft newNodeDraft() {
        NodeDraft node = new NodeDraftImpl(this);
        //TODO add node to tmp graph
        return node;
    }

    public void addNode(NodeDraft node) {
        nodeMap.put(node.getId(), node);
    }

    public EdgeDraft newEdgeDraft() {
        EdgeDraft edge = new EdgeDraftImpl();
        return edge;
    }

    public EdgeDraft newEdgeDraft(String id) {
        EdgeDraft edge = new EdgeDraftImpl();
        return edge;
    }

    public void addEdge(EdgeDraft edge) {
        edgeMap.add(edge);
    }

    public NodeDraft getNode(String id) {
        return nodeMap.get(id);
    }

    public void setEdgeDefault(EdgeType edgeDefault) {
        this.edgeDefault = edgeDefault;
    }

    public int getNumNodes() {
        return nodeMap.size();
    }

    public int getNumEdges() {
        return edgeMap.size();
    }

}
