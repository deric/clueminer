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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeIterable implements EdgeIterable {

    private final List<Edge> edges;

    public AdjListEdgeIterable(Map<Long, Edge> edges) {
        this.edges = new LinkedList<>(edges.values());
    }

    public AdjListEdgeIterable(List<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }

    @Override
    public Edge[] toArray() {
        return edges.toArray(new Edge[0]);
    }

    @Override
    public Collection<Edge> toCollection() {
        return edges;
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return edges.size();
    }

}
