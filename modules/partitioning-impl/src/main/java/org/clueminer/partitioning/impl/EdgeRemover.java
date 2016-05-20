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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
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
    public Graph removeEdges(Graph originalGraph, ArrayList<ArrayList<Node>> partitions) {
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

}
