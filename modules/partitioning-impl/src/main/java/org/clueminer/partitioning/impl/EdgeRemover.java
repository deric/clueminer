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
package org.clueminer.partitioning.impl;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Bruna
 */
public class EdgeRemover {

    /**
     * Identify edges which connect different partitions in the given graph and
     * return new graph without these edges.
     *
     * @param originalGraph
     * @param partitions
     * @return new graph without edges crossing different partitions
     */
    public static Graph removeEdges(Graph originalGraph, ArrayList<ArrayList<Node>> partitions) {
        Graph result = null;
        try {
            //create instance of same graph storage implementation
            result = originalGraph.getClass().newInstance();
            result.ensureCapacity(originalGraph.getNodeCount());

            for (Node node : originalGraph.getNodes()) {
                result.addNode(node);
            }

            for (ArrayList<Node> partition : partitions) {
                for (int i = 0; i < partition.size(); i++) {
                    for (int j = i + 1; j < partition.size(); j++) {
                        if (originalGraph.isAdjacent(partition.get(i), partition.get(j))) {
                            result.addEdge(originalGraph.getEdge(partition.get(i), partition.get(j)));
                        }
                    }
                }
            }

        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }

        return result;
    }

    public static Graph safeRemoveEdges(Graph originalGraph, ArrayList<ArrayList<Node>> partitions) {
        Graph result = null;
        try {
            //create instance of same graph storage implementation
            int nodeCnt = originalGraph.getNodeCount();
            result = originalGraph.getClass().newInstance();
            result.ensureCapacity(nodeCnt);

            GraphBuilder f = result.getFactory();
            Int2LongOpenHashMap mapping = new Int2LongOpenHashMap(nodeCnt);
            Node nn;
            for (Node node : originalGraph.getNodes()) {
                nn = f.newNode(node.getInstance());
                mapping.put(node.getInstance().getIndex(), nn.getId());
                result.addNode(nn);
            }

            long ida, idb;
            Node na, nb;
            for (ArrayList<Node> partition : partitions) {
                for (int i = 0; i < partition.size(); i++) {
                    na = partition.get(i);
                    ida = mapping.get(na.getInstance().getIndex());
                    for (int j = i + 1; j < partition.size(); j++) {
                        nb = partition.get(j);
                        if (originalGraph.isAdjacent(na, nb)) {
                            idb = mapping.get(nb.getInstance().getIndex());
                            result.addEdge(f.newEdge(result.getNode(ida), result.getNode(idb)));
                        }
                    }
                }
            }

        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }

        return result;
    }

}
