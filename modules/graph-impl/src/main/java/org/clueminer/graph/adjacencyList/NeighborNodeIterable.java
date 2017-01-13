/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author deric
 */
public class NeighborNodeIterable implements NodeIterable {

    private final Set<Neighbor> neighbors;

    public NeighborNodeIterable(Set<Neighbor> set) {
        this.neighbors = set;
    }

    @Override
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {

            private Iterator<Neighbor> it = neighbors.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Node next() {
                Neighbor n = it.next();
                return n.node;
            }

        };
    }

    @Override
    public Node[] toArray() {
        Node[] res = new Node[neighbors.size()];
        int i = 0;
        for (Neighbor n : neighbors) {
            res[i++] = n.node;
        }
        return res;
    }

    @Override
    public Collection<Node> toCollection() {
        ArrayList<Node> res = new ArrayList<>(neighbors.size());
        for (Neighbor n : neighbors) {
            res.add(n.node);
        }
        return res;
    }

    @Override
    public int size() {
        return neighbors.size();
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
