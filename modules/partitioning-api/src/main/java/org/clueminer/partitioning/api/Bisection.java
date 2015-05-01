package org.clueminer.partitioning.api;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public interface Bisection {

    public String getName();

    /**
     * Bisect the graph
     *
     * @param g graph to partition
     * @return list of nodes for each cluster
     */
    public ArrayList<LinkedList<Node>> bisect(Graph g);

    /**
     * Bisect the graph
     *
     * @return list of nodes for each cluster
     */
    public ArrayList<LinkedList<Node>> bisect();

    /**
     * Remove edges between clusters which were created by bisection
     *
     * @return bisected graph
     */
    public Graph removeUnusedEdges();
}
