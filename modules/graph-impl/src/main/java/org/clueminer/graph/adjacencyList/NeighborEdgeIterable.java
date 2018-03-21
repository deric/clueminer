/*
 * Copyright (C) 2011-2018 clueminer.org
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
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author deric
 */
public class NeighborEdgeIterable implements EdgeIterable {

    private final Set<Neighbor> neighbors;

    public NeighborEdgeIterable(Set<Neighbor> set) {
        this.neighbors = set;
    }

    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {

            private Iterator<Neighbor> it = neighbors.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Edge next() {
                Neighbor n = it.next();
                return n.edge;
            }

        };
    }

    @Override
    public Edge[] toArray() {
        Edge[] res = new Edge[neighbors.size()];
        int i = 0;
        for (Neighbor n : neighbors) {
            res[i++] = n.edge;
        }
        return res;
    }

    @Override
    public Collection<Edge> toCollection() {
        ArrayList<Edge> res = new ArrayList<>(neighbors.size());
        for (Neighbor n : neighbors) {
            res.add(n.edge);
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
