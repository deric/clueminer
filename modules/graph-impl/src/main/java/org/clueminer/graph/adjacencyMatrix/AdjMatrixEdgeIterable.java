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
package org.clueminer.graph.adjacencyMatrix;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author tomas
 */
public class AdjMatrixEdgeIterable implements EdgeIterable {

    private final Edge[] edges;

    public AdjMatrixEdgeIterable(Edge[] edges) {
        this.edges = edges;
    }

    @Override
    public Iterator<Edge> iterator() {
        return new AdjMatrixEdgeIterator();
    }

    @Override
    public Edge[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Edge> toCollection() {
        return Arrays.asList(edges);
    }

    @Override
    public void doBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return edges.length;
    }

    private class AdjMatrixEdgeIterator implements Iterator<Edge> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < edges.length;
        }

        @Override
        public Edge next() {
            return edges[currentIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
