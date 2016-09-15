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
package org.clueminer.graph.fast;

/**
 *
 * @author deric
 */
public class GraphVersion {

    protected final FastGraph graph;
    protected int nodeVersion = Integer.MIN_VALUE + 1;
    protected int edgeVersion = Integer.MIN_VALUE + 1;

    public GraphVersion(FastGraph graph) {
        this.graph = graph;
    }

    public int incrementAndGetNodeVersion() {
        nodeVersion++;
        if (nodeVersion == Integer.MAX_VALUE) {
            nodeVersion = Integer.MIN_VALUE + 1;
        }
        return nodeVersion;
    }

    public int incrementAndGetEdgeVersion() {
        edgeVersion++;
        if (edgeVersion == Integer.MAX_VALUE) {
            edgeVersion = Integer.MIN_VALUE + 1;
        }
        return edgeVersion;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.nodeVersion;
        hash = 17 * hash + this.edgeVersion;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphVersion other = (GraphVersion) obj;
        if (this.nodeVersion != other.nodeVersion) {
            return false;
        }
        return this.edgeVersion == other.edgeVersion;
    }
}
