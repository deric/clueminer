/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
