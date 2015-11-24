package org.clueminer.partitioning.api;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Bruna
 */
public interface Bisection {

    String getName();

    /**
     * Bisect the graph
     *
     * @param g graph to partition
     * @param params
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node>> bisect(Graph g, Props params);

    /**
     * Bisect the graph
     *
     * @param params
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node>> bisect(Props params);

    /**
     * Remove edges between clusters which were created by bisection
     *
     * @return bisected graph
     */
    Graph removeUnusedEdges();
}
