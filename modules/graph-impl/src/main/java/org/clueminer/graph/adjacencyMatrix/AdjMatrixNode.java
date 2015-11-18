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

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;

/**
 *
 * @author tomas
 */
public class AdjMatrixNode implements Node {

    private final long id;
    Object label;
    private Instance instance;

    public AdjMatrixNode(long id) {
        this.id = id;
    }

    public AdjMatrixNode(long id, Object label) {
        this.label = label;
        this.id = id;
    }

    public AdjMatrixNode(long id, Instance i) {
        instance = i;
        this.id = id;
    }

    @Override
    public void setInstance(Instance i) {
        instance = i;
    }

    @Override
    public Instance getInstance() {
        return instance;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getLabel() {
        return label;
    }

}
