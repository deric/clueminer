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
package org.clueminer.dataset.api;

/**
 * Used for defining attributes in a dataset. Supported attributes types are
 * loaded in runtime, which makes it easy to create a new type.
 *
 * @author Tomas Barton
 * @param <A> type of attribute being created
 */
public interface AttributeBuilder<A extends Attribute> {

    /**
     * By default should create a numeric continuous attribute (role: input)
     *
     * @param name
     * @param type
     * @return
     */
    A create(String name, AttributeType type);

    /**
     *
     * @param name
     * @param type
     * @param role role is either input data (processed by algorithms) or meta
     *             data
     * @return
     */
    A create(String name, AttributeType type, AttributeRole role);

    /**
     * By default should create a numeric attribute with input data role
     *
     * @param name
     * @param type
     * @return
     */
    A create(String name, String type);

    /**
     * In order to be independent on specific implementation a lookup by type
     * and role could be used. Befare of possible runtime exceptions
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    A create(String name, String type, String role);

    /**
     * Creates new instance of an attribute, but does not add it to the dataset;
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    A build(String name, String type, String role);

    A build(String name, AttributeType type, AttributeRole role);

    A build(String name, String type);

    A build(String name, AttributeType type);
}
