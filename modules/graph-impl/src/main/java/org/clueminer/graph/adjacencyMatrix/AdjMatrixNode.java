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

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.impl.ElemImpl;

/**
 *
 * @author tomas
 * @param <E>
 */
public class AdjMatrixNode<E extends Instance> extends ElemImpl implements Node<E> {

    private E instance;

    public AdjMatrixNode(long id) {
        super(id);
    }

    public AdjMatrixNode(long id, Object label) {
        super(id);
        this.label = label;
    }

    public AdjMatrixNode(long id, E i) {
        super(id);
        instance = i;
    }

    @Override
    public void setInstance(E i) {
        instance = i;
    }

    @Override
    public E getInstance() {
        return instance;
    }

}
