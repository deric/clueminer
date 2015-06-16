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
package org.clueminer.graph.fast;

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public class NodeImpl extends ElementImpl implements Node {

    private Instance instance;

    protected int inDegree;
    protected int outDegree;
    protected int mutualDegree;

    public NodeImpl(Long id, FastGraph graphStore) {
        super(id, graphStore);
        this.attributes = new Object[1];
    }

    public NodeImpl(Object id) {
        this((long) id, null);
    }

    @Override
    public Object getLabel() {
        return attributes[FastGraphConfig.ELEMENT_LABEL_INDEX];
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

}
