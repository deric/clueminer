package org.clueminer.partitioning.api;

import java.util.ArrayList;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;

/**
 * Partitioning algorithm should divide given graph into equally sized coherent
 * components.
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
     * @param params optional parameters
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node<E>>> partition(int k, Graph g, Props params);

    /**
     * Algorithm for bisection
     *
     * @param bisection
     */
    void setBisection(Bisection bisection);
}
