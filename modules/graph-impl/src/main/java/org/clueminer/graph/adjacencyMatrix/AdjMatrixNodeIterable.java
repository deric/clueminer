/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.graph.adjacencyMatrix;

import java.util.ArrayList;
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

    private final ArrayList<Node> nodes;


    public AdjMatrixNodeIterable(Node[] nodes) {
        this.nodes = new ArrayList<>(Arrays.asList(nodes));
    }
    
    public AdjMatrixNodeIterable(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Iterator<Node> iterator() {
        Iterator<Node> it = new Iterator<Node>() {
            
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < nodes.size();
            }

            @Override
            public Node next() {
                return nodes.get(currentIndex++);
            }
        };
        return it;
    }

    @Override
    public Node[] toArray() {
        Node[] array = new Node[nodes.size()];
        for (int i = 0;i<nodes.size();i++) {
            array[i] = nodes.get(i);
        }
        return array;
    }

    @Override
    public Collection<Node> toCollection() {
        return nodes;
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
