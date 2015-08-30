package org.clueminer.partitioning.api;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 * @param <E>
 */
public interface Partitioning<E extends Instance> {

    String getName();

    /**
     * Partition the graph
     *
     * @param k
     * @param g graph to partition
     * @return list of nodes for each cluster
     */
    ArrayList<LinkedList<Node<E>>> partition(int k, Graph g);

    public void setBisection(Bisection bisection);
}
