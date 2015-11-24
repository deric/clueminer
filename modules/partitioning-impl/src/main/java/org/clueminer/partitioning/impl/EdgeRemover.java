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
