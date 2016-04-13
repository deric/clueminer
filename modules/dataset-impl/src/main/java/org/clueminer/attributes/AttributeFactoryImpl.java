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
package org.clueminer.attributes;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.api.Dataset;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class AttributeFactoryImpl<E> implements AttributeBuilder {

    private final Dataset<? extends E> target;

    public AttributeFactoryImpl(Dataset<? extends E> target) {
        this.target = target;
    }

    /**
     * Creates a simple single attribute depending on the given value type.
     *
     * @param name
     * @param type
     * @return
     */
    @Override
    public Attribute create(String name, AttributeType type) {
        return create(name, type, BasicAttrRole.INPUT);
    }

    @Override
    public Attribute create(String name, String type) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("attribute name can not be empty");
        }
        if (type == null || type.isEmpty()) {
            throw new RuntimeException("attribute type can not be empty");
        }
        return create(name, BasicAttrType.valueOf(type));
    }

    /**
     * Create attribute and add it to the dataset (if target is not null)
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    @Override
    public Attribute create(String name, AttributeType type, AttributeRole role) {
        Attribute ret = build(name, type, role);
        add(ret);
        return ret;
    }

    private void add(Attribute attr) {
        if (target != null) {
            target.addAttribute(attr);
        }
    }

    @Override
    public Attribute create(String name, String type, String role) {
        return create(name, BasicAttrType.valueOf(type), BasicAttrRole.valueOf(role));
    }

    @Override
    public Attribute build(String name, AttributeType type, AttributeRole role) {
        Attribute ret;
        switch ((BasicAttrType) type) {
            case NUMERICAL:
            case NUMERIC:
            case INTEGER:
            case REAL: //right now it's handled the very same way
                return new NumericalAttribute(name, role);
            case STRING:
                return new StringAttribute(name, role);
            default:
                throw new RuntimeException("attribute type " + type + " is not supported");
        }
    }

    @Override
    public Attribute build(String name, String type, String role) {
        return build(name, BasicAttrType.valueOf(type), BasicAttrRole.valueOf(role));
    }

    @Override
    public Attribute build(String name, String type) {
        return build(name, BasicAttrType.valueOf(type), BasicAttrRole.INPUT);
    }

    @Override
    public Attribute build(String name, AttributeType type) {
        return build(name, type, BasicAttrRole.INPUT);
    }
}
