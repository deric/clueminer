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

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeType;
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
    protected int type;

    protected int storeId = EdgeStore.NULL_ID;
    protected int nextOutEdge = EdgeStore.NULL_ID;
    protected int nextInEdge = EdgeStore.NULL_ID;
    protected int previousOutEdge = EdgeStore.NULL_ID;
    protected int previousInEdge = EdgeStore.NULL_ID;

    protected byte flags;

    public EdgeImpl(long id, Node source, Node target) {
        super(id, null);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = 1.0;
        this.type = EdgeType.NONE.getValue();
    }

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = 1.0;
        this.type = EdgeType.NONE.getValue();
    }

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target, int type) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = 1.0;
        this.type = type;
    }

    public EdgeImpl(long id, FastGraph graphStore, Node source, Node target, int type, double weight) {
        super(id, graphStore);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        this.type = type;
    }

    public EdgeImpl(long id, Node source, Node target, int type, double weight) {
        super(id, null);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        this.type = type;
    }

    public EdgeImpl(long id, Node source, Node target, double weight, boolean directed) {
        super(id, null);
        this.source = (NodeImpl) source;
        this.target = (NodeImpl) target;
        this.weight = weight;
        this.type = EdgeType.NONE.getValue();
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int id) {
        this.storeId = id;
    }

    @Override
    public boolean isDirected() {
        return type != EdgeType.NONE.getValue();
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
    public EdgeType getDirection() {
        return EdgeType.values()[type];
    }

    @Override
    public void setDirection(EdgeType direction) {
        this.type = direction.getValue();
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

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 13 + storeId;
        hash += 29 * source.hashCode();
        hash += 967 * target.hashCode();

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
        final EdgeImpl other = (EdgeImpl) obj;
        return this.hashCode() == other.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EdgeIml{").append(storeId).append("} ")
                .append(source).append(" -> ").append(target);
        return sb.toString();
    }

}
