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
package org.clueminer.dataset.api;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public class AttributeDescription implements Serializable {

    private static final long serialVersionUID = 2262660608205211256L;
    private String name;
    /**
     * The default value for this Attribute.
     */
    private double defaultValue = 0.0;
    /**
     * Index of this attribute in its Dataset
     */
    private int index;
    private AttributeType type;

    public AttributeDescription(String name, AttributeType type, double defaultValue) {
        this.name = name;
        this.index = -1;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public AttributeDescription(int index, String name, AttributeType type, double defaultValue) {
        this.name = name;
        this.index = index;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    AttributeDescription(AttributeDescription other) {
        this.name = other.name;
        this.type = other.type;
        this.defaultValue = other.defaultValue;
        this.index = other.index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Default value of an attribute
     *
     * @return
     */
    public double getDefault() {
        return defaultValue;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public void setDefault(double value) {
        defaultValue = value;
    }

    @Override
    public Object clone() {
        return new AttributeDescription(this);
    }

    /**
     * Returns true if the given attribute has the same name and the same table
     * index.
     *
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeDescription)) {
            return false;
        }
        AttributeDescription a = (AttributeDescription) o;
        return this.name.equals(a.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ Integer.valueOf(this.index).hashCode();
    }
}
