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
package org.clueminer.math.matrix;

import java.util.Arrays;
import org.clueminer.math.IntMatrix;

/**
 *
 * @author deric
 */
public class IntegerMatrix implements IntMatrix {

    private final int[][] A;
    private final int m;
    private final int n;

    public IntegerMatrix(int m, int n) {
        this.m = m;
        this.n = n;
        A = new int[m][n];
    }

    /**
     * Construct an m-by-n constant matrix.
     *
     * @param m Number of rows.
     * @param n Number of columns.
     * @param s Fill the matrix with this scalar value.
     */
    public IntegerMatrix(int m, int n, int s) {
        this.m = m;
        this.n = n;
        A = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(A[i], s);
        }
    }

    @Override
    public int get(int i, int j) {
        return A[i][j];
    }

    @Override
    public void set(int i, int j, int val) {
        A[i][j] = val;
    }

    /**
     * Get row dimension.
     *
     * @return m, the number of rows.
     */
    @Override
    public int rowsCount() {
        return m;
    }

    /**
     * Get column dimension.
     *
     * @return n, the number of columns.
     */
    @Override
    public int columnsCount() {
        return n;
    }

}
