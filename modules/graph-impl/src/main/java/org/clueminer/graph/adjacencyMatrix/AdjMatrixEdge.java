/*
 * Copyright (C) 2011-2015 clueminer.org
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

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Tomas Bruna
 */
public class AdjMatrixEdge implements Edge {

    private final long id;
    private final Node source;
    private final Node target;
    private boolean directed;
    private double weight;
    private Object label;

    AdjMatrixEdge(long id, Node source, Node target, double weight) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public Object getLabel() {
        return this.label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
