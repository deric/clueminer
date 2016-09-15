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

    protected static final byte DIRECTED_BYTE = 1;
    protected static final byte MUTUAL_BYTE = 1 << 1;

    protected final NodeImpl source;
    protected final NodeImpl target;
    protected double weight;
    protected Direction direction;
    protected int storeId = EdgeStore.NULL_ID;

    protected byte flags;

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target, double weight) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        this.direction = Direction.NONE;
    }

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target, double weight, Direction dir) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        this.direction = dir;
    }

    public EdgeImpl(long id, Node source, Node target, double weight, boolean directed) {
        super(id, null);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        if (directed) {
            this.direction = Direction.FORWARD;
        } else {
            this.direction = Direction.NONE;
        }
    }

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target, double weight, boolean directed) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        if (directed) {
            this.direction = Direction.FORWARD;
        } else {
            this.direction = Direction.NONE;
        }
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int id) {
        this.storeId = id;
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

    public boolean isSelfLoop() {
        return source == target;
    }

    protected void setMutual(boolean mutual) {
        if (isDirected()) {
            if (mutual) {
                flags |= MUTUAL_BYTE;
            } else {
                flags &= ~MUTUAL_BYTE;
            }
        }
    }

    protected boolean isMutual() {
        return (flags & MUTUAL_BYTE) == MUTUAL_BYTE;
    }

    public long getLongId() {
        return EdgeStore.getLongId(source, target, isDirected());
    }

}
