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
    
     /**
     * Partition the graph
     *
     * @return list of nodes for each cluster
     */
    public ArrayList<LinkedList<Node>> partition();
    
    /**
     * Remove edges between clusters which were created by partitioning
     *
     * @return partitioned graph
     */
    public Graph removeUnusedEdges();
    
}
