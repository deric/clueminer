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

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class NodeImpl extends ElementImpl implements Node {

    protected EdgeImpl[] headOut = new EdgeImpl[FastGraphConfig.EDGESTORE_DEFAULT_TYPE_COUNT];
    protected EdgeImpl[] headIn = new EdgeImpl[FastGraphConfig.EDGESTORE_DEFAULT_TYPE_COUNT];

    private Instance instance;

    protected int inDegree;
    protected int outDegree;
    protected int mutualDegree;
    protected int storeId = NodeStore.NULL_ID;

    public NodeImpl(Long id) {
        super(id, null);
        this.attributes = new Object[1];
    }

    public NodeImpl(Long id, FastGraph graphStore) {
        super(id, graphStore);
        this.attributes = new Object[1];
    }

    public NodeImpl(Long id, FastGraph graphStore, Object label) {
        super(id, graphStore);
        setLabel(label);
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int id) {
        this.storeId = id;
    }

    @Override
    public void setInstance(Instance i) {
        this.instance = i;
    }

    @Override
    public Instance getInstance() {
        return instance;
    }

    public int getDegree() {
        return inDegree + outDegree;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public int getUndirectedDegree() {
        return inDegree + outDegree - mutualDegree;
    }

    @Override
    public int hashCode() {
        int hash = 3 + storeId;
        hash *= id + 7;
        if (instance != null) {
            hash = 53 * hash + instance.hashCode();
        }

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

        final NodeImpl other = (NodeImpl) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NodelImpl [").append(storeId).append("]");
        return sb.toString();
    }


}
