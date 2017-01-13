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

import java.util.ArrayList;
import java.util.Iterator;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class FloodFill {

    private Graph graph;
    private boolean marked[];
    private ArrayList<ArrayList<Node>> result;

    /**
     * Finds disconnected subgraphs in the given graph
     *
     * @param g Graph to find subgraphs in
     * @param maxPartition expected size of max. partition
     * @return Lists of nodes in different subgraphs
     */
    public ArrayList<ArrayList<Node>> findSubgraphs(Graph g, int maxPartition) {
        graph = g;
        marked = new boolean[graph.getNodeCount()];

        for (Node node : graph.getNodes()) {
            marked[graph.getIndex(node)] = false;
        }

        boolean allMarked = false;
        int clusterCounter = 0;
        result = new ArrayList<>();
        while (!allMarked) {
            allMarked = true;
            for (Node node : graph.getNodes()) {
                if (marked[graph.getIndex(node)] == false) {
                    allMarked = false;
                    result.add(new ArrayList<Node>(maxPartition));
                    markSubgraph(node, clusterCounter++);
                }
            }
        }
        return result;
    }

    /**
     * Recursively marks all nodes in subgraph
     *
     * @param node
     * @param clusterNumber
     */
    private void markSubgraph(Node node, int clusterNumber) {
        result.get(clusterNumber).add(node);
        marked[graph.getIndex(node)] = true;
        Iterator<Node> neighbors = graph.getNeighbors(node).iterator();
        while (neighbors.hasNext()) {
            Node neighbor = neighbors.next();
            if (marked[graph.getIndex(neighbor)] == false) {
                markSubgraph(neighbor, clusterNumber);
            }
        }
    }
}
