/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.math;

/**
 * A matrix with Integer precision
 *
 * @author deric
 */
public interface IntMatrix {

    /**
     * Get value from i-th row and j-th column
     *
     * @param i row index
     * @param j column index
     * @return
     */
    int get(int i, int j);

    /**
     * Set value
     *
     * @param i
     * @param j
     * @param val
     */
    void set(int i, int j, int val);

    /**
     * Get row dimension.
     *
     * @return number of rows
     */
    int rowsCount();

    /**
     * Get column dimension.
     *
     * @return number of columns
     */
    int columnsCount();

}
