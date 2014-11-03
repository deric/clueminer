package org.clueminer.graph.api;

/**
 *
 * @author Tomas Barton
 */
public interface Element {

    /**
     * Unique node ID
     *
     * @return
     */
    long getId();
    
    /**
     * Node label
     *
     * @return
     */
    Object getLabel();

}
