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

import org.clueminer.dataset.api.Attribute;

/**
 * Draft is used during data import.
 *
 * @author Tomas Barton
 */
public interface AttributeDraft extends Attribute {

    Class<?> getJavaType();

    void setJavaType(Class<?> t);

    void setMeta(boolean b);

    void setNumerical(boolean b);

    boolean isUnique();

    void setUnique(boolean b);

    Object getDefaultValue();

    void setDefaultValue(Object value);

    /**
     *
     * @return true when attribute shouldn't be imported
     */
    boolean isSkipped();

    void setSkipped(boolean b);

}
