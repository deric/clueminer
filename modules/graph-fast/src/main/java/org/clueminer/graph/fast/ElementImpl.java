/*
 * Copyright (C) 2011-2018 clueminer.org
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
    //numeric ID
    protected Long id;
    protected Object[] attributes;

    public ElementImpl(Long id, FastGraph graphStore) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.graphStore = graphStore;
        this.attributes = new Object[FastGraphConfig.ELEMENT_LABEL_INDEX + 1];
        this.id = id;
    }

    public ElementImpl(Object id, FastGraph graphStore) {
        if (id == null) {
            throw new NullPointerException();
        }
        this.graphStore = graphStore;
        this.attributes = new Object[FastGraphConfig.ELEMENT_LABEL_INDEX + 1];
        //using any type
        this.attributes[FastGraphConfig.ELEMENT_ID_INDEX] = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLabel() {
        if (attributes.length > FastGraphConfig.ELEMENT_LABEL_INDEX) {
            return (String) attributes[FastGraphConfig.ELEMENT_LABEL_INDEX];
        }
        return null;
    }

    public final void setLabel(Object label) {
        int index = FastGraphConfig.ELEMENT_LABEL_INDEX;
        synchronized (this) {
            if (index >= attributes.length) {
                Object[] newArray = new Object[index + 1];
                System.arraycopy(attributes, 0, newArray, 0, attributes.length);
                attributes = newArray;
            }
            attributes[index] = label;
        }
    }

    @Override
    public Object getAttribute(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object removeAttribute(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearAttributes() {
        Object[] newAttributes = new Object[FastGraphConfig.ELEMENT_ID_INDEX + 1];
        newAttributes[FastGraphConfig.ELEMENT_ID_INDEX] = attributes[FastGraphConfig.ELEMENT_ID_INDEX];
        attributes = newAttributes;
    }

}
