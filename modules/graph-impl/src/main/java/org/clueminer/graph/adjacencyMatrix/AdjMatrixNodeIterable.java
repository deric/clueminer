/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author tomas
 */
public class AdjMatrixNodeIterable implements NodeIterable {

    private final Node[] nodes;
    private final int nodeCnt;

    public AdjMatrixNodeIterable(Node[] nodes, int totalCnt) {
        this.nodes = nodes;
        this.nodeCnt = totalCnt;
    }

    @Override
    public Iterator<Node> iterator() {
        Iterator<Node> it = new Iterator<Node>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < nodeCnt;
            }

            @Override
            public Node next() {
                return nodes[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        return it;
    }

    public int size() {
        return nodeCnt;
    }

    @Override
    public Node[] toArray() {
        return nodes;
    }

    @Override
    public Collection<Node> toCollection() {
        return Arrays.asList(nodes);
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
