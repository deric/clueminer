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
package org.clueminer.importer.impl;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.io.importer.api.AttributeDraft;

/**
 *
 * @author deric
 */
public class AttributeDraftBuilder implements AttributeBuilder<AttributeDraft> {

    private DraftContainer container;
    private Class<?> defaultNumericType = Double.class;

    public AttributeDraftBuilder(DraftContainer container) {
        this.container = container;
    }

    public AttributeDraft create(int index, String name) {
        return create(index, name, BasicAttrType.NUMERIC, BasicAttrRole.INPUT);
    }

    public AttributeDraft create(int index, String name, AttributeType type, AttributeRole role) {
        AttributeDraft attr;
        //try to avoid duplicate attribute
        if (!container.hasAttributeAtIndex(index)) {
            attr = new AttributeDraftImpl(name);
            attr.setIndex(index);
            attr.setJavaType(toJavaType((BasicAttrType) type));
            attr.setType(type);
            attr.setRole(role);
            container.attributeMap.put(name, attr);
            container.attributeList.put(index, attr);
        } else {
            attr = (AttributeDraft) container.attributeList.get(index);
            if (!attr.getName().equals(name)) {
                //update attribute's names map
                container.attributeMap.remove(attr.getName());
                attr.setName(name);
                container.attributeMap.put(name, attr);
            }
        }
        return attr;
    }

    @Override
    public AttributeDraft create(String name, AttributeType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft create(String name, AttributeType type, AttributeRole role) {
        return create(container.attributeCount(), name, type, role);
    }

    @Override
    public AttributeDraft create(String name, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft create(String name, String type, String role) {
        AttributeType attrType = BasicAttrType.valueOf(type);
        return create(container.attributeCount(), name, attrType, convertRole(role));
    }

    @Override
    public AttributeDraft build(String name, String type, String role) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft build(String name, AttributeType type, AttributeRole role) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft build(String name, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft build(String name, AttributeType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected AttributeType convertType(Object klass) {
        BasicAttrType type = BasicAttrType.NUMERIC;
        if (klass instanceof String) {
            type = BasicAttrType.STRING;
        }
        return type;
    }

    protected AttributeRole convertRole(String role) {
        return BasicAttrRole.valueOf(role);
    }

    public Class<?> toJavaType(BasicAttrType type) {
        switch (type) {
            case STRING:
                return String.class;
            case REAL:
            case NUMERIC:
                return Double.class;
            default:
                return Double.class;
        }
    }

}
