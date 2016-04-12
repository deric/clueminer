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
package org.clueminer.io.importer.api;

/**
 *
 * @author Tomas Barton
 * @deprecated logic will be moved into storing backends
 */
public interface AttributeParser {

    /**
     * Return common type name
     *
     * @return
     */
    String getName();

    /**
     * Try to parse given Object (possibly String)
     *
     * @param value
     * @return
     * @throws org.clueminer.io.importer.api.ParsingError
     */
    Object parse(String value) throws ParsingError;

    /**
     * Default value is used when parsing fails
     *
     * @return String representation of default "NULL" value (e.g. n/a)
     */
    String getNullValue();
}
