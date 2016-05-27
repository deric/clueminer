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
package org.clueminer.dataset.api;

import java.util.Iterator;

/**
 * Generic data storage (e.g. row/column in a spreadsheet)
 *
 * @author Tomas Barton
 */
public interface DataVector {

    /**
     * Human readable name, if any
     *
     * @return string identification
     */
    String getName();

    /**
     *
     * @return length of the vector
     */
    int size();

    /**
     *
     * @return iterator over all elements
     */
    Iterator<? extends Object> values();

    /**
     * Id of row/column
     *
     * @return non-negative index starting from 0
     */
    int getIndex();
}
