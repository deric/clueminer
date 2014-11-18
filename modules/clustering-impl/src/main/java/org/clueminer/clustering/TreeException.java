package org.clueminer.clustering;

import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class TreeException extends RuntimeException {

    private final DendroNode nodeA, nodeB;

    public TreeException(DendroNode nodeA, DendroNode nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public DendroNode getNodeA() {
        return nodeA;
    }

    public DendroNode getNodeB() {
        return nodeB;
    }

}
