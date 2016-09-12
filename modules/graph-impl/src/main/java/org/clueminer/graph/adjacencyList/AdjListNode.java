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

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.impl.ElemImpl;

/**
 *
 * @author Hamster
 * @param <E>
 */
public class AdjListNode<E extends Instance> extends ElemImpl implements Node<E> {

    private E instance;

    public AdjListNode(long id) {
        super(id);
    }

    public AdjListNode(long id, Object label) {
        super(id);
        this.label = label;
    }

    @Override
    public void setInstance(E instance) {
        this.instance = instance;
    }

    @Override
    public E getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("n");
        builder.append(id);
        builder.append(": [");
        builder.append(this.getInstance() != null ? this.getInstance().classValue() : "?");
        builder.append("]\n");
        return builder.toString();
    }

}
