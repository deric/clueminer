package org.clueminer.graph.api;

/**
 *
 * @author Tomas Barton
 */
public interface Edge {

    Object getId();

    /**
     * True when edge is directed
     *
     * @return
     */
    boolean isDirected();

    Node getSource();

    Node getTarget();
}
