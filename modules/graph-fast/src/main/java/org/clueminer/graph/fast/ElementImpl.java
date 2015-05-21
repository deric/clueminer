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

import org.clueminer.graph.api.Element;

/**
 *
 * @author deric
 */
public abstract class ElementImpl implements Element {

    protected final FastGraph graphStore;
    protected Long id;

    public ElementImpl(Long id, FastGraph graphStore) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.graphStore = graphStore;
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

}
