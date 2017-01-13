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
package org.clueminer.graph.impl;

import java.util.HashMap;
import org.clueminer.graph.api.Element;

/**
 *
 * @author deric
 */
public abstract class ElemImpl implements Element {

    protected final long id;
    protected Object label;
    private final HashMap<String, Object> attributes;

    public ElemImpl(long id) {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getLabel() {
        return label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public void clearAttributes() {
        attributes.clear();
    }

}
