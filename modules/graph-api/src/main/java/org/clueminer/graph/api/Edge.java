package org.clueminer.graph.api;

/**
 *
 * @author Tomas Barton
 */
public interface Edge extends Element {

    /**
     * True when edge is directed
     *
     * @return
     */
    boolean isDirected();

    Node getSource();

    Node getTarget();
}
