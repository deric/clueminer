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
package org.clueminer.math;

/**
 *
 * @author Tomas Barton
 */
public interface SparseMatrix extends Matrix {

    /**
     * Returns the column as a sparse vector. Whether updates to the vector are
     * written through to the backing matrix is left open to the implementation.
     *
     * @param column The column to return a {@code DoubleVector} for
     *
     * @return A {@code SparseVector} representing the column at {@code column}
     */
    SparseVector getColumnSparseVector(int column);

    /**
     * Returns the row as a sparse vector. Whether updates to the vector are
     * written through to the backing matrix is left open to the implementation.
     *
     * @param row the index of row to return
     *
     * @return A {@code SparseVector} of the row's data
     */
    SparseVector getRowSparseVector(int row);
}
