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
package org.clueminer.graph.adjacencyList;

import java.util.Objects;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class Neighbor implements Comparable<Neighbor> {

    Edge edge;
    Node node;

    public Neighbor(Edge e, Node n) {
        edge = e;
        node = n;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o.getClass().equals(Neighbor.class))) {
            return false;
        }
        Neighbor other = (Neighbor) o;
        return node.getId() == other.node.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.node);
        return hash;
    }

    @Override
    public int compareTo(Neighbor o) {
        return (int) (node.getId() - o.node.getId());
    }

}
