package org.clueminer.partitioning.api;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public interface Partitioning {

    String getName();

    /**
     * Partition the graph
     *
     * @param k
     * @param g graph to partition
     * @return list of nodes for each cluster
     */
    ArrayList<LinkedList<Node>> partition(int k, Graph g);

    public void setBisection(Bisection bisection);
}
