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

import org.clueminer.graph.api.Direction;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class EdgeImpl extends ElementImpl implements Edge {

    protected final Node source;
    protected final Node target;
    protected double weight;
    protected Direction direction;

    public EdgeImpl(Long id, FastGraph graphStore, Node source, Node target, double weight) {
        super(id, graphStore);
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.direction = Direction.NONE;
    }

    public EdgeImpl(Long id, FastGraph graphStore, Node source, Node target, double weight, boolean directed) {
        super(id, graphStore);
        this.source = source;
        this.target = target;
        this.weight = weight;
        if (directed) {
            this.direction = Direction.FORWARD;
        } else {
            this.direction = Direction.NONE;
        }
    }

    @Override
    public boolean isDirected() {
        return direction != Direction.NONE;
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
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}
