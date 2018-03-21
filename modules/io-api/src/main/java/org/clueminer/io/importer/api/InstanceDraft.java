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
package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.Instance;

/**
 * Stores data during import before casted to actual types.
 *
 * @author Tomas Barton
 */
public interface InstanceDraft extends Instance {

    void setType(Object type);

    Object getType();

    /**
     * Value for attribute specified by the key
     *
     * @param key attribute identification
     * @return
     */
    Object getValue(String key);

    /**
     * Value of ith attribute
     *
     * @param i th attribute
     * @return
     */
    Object getObject(int i);

    void setObject(String key, Object value);

    void setObject(int index, Object value);

}
