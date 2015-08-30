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
package org.clueminer.chameleon;

/**
 * Linear array storage of a triple - EIC, ECL and a counter. Instead of of n*n
 * matrix we use n*(n-1)/2 array.
 *
 * @author deric
 */
public class GraphPropertyStore {

    /**
     * indexes in the double array
     */
    public static final int EIC = 0;
    public static final int ECL = 1;
    public static final int CNT = 2;

    private final double[][] store;

    public GraphPropertyStore(int capacity) {
        store = new double[triangleSize(capacity)][3];
    }

    /**
     * Compute size of triangular matrix (n x n) minus diagonal
     *
     * @param n number of rows (or columns) for square matrix
     * @return
     */
    private int triangleSize(int n) {
        return ((n - 1) * n) >>> 1;
    }

    /**
     * Return an index where is actually item stored
     *
     * A simple hash function for storing lower triangular matrix in one
     * dimensional array
     *
     * i should not be equal to j (diagonal numbers are not stored!)
     *
     * @param i row index
     * @param j column index
     * @return index in one-dimensional array
     */
    private int map(int i, int j) {
        if (i < j) {
            /**
             * swap variables, matrix is symmetrical, we work with lower
             * triangular matrix
             */
            int tmp = i;
            i = j;
            j = tmp;
        }
        /**
         * it's basically a sum of arithmetic row (we need to know how many
         * numbers could be allocated before given position [x,y])
         */
        return triangleSize(i) + j;
    }

    public double getEIC(int i, int j) {
        return store[map(i, j)][EIC];
    }

    public double getECL(int i, int j) {
        return store[map(i, j)][ECL];
    }

    /**
     * Counter value - number of edges that contributed to EIC weights sum
     *
     * @param i
     * @param j
     * @return
     */
    public double getCnt(int i, int j) {
        return store[map(i, j)][CNT];
    }

    /**
     * Update interconnectivity and closeness values
     *
     * @param i
     * @param j
     * @param edgeWeight
     */
    public void updateWeight(int i, int j, double edgeWeight) {
        if (i == j) {
            throw new IllegalArgumentException("diagonal items are not writable");
        }
        store[map(i, j)][EIC] += edgeWeight;
        store[map(i, j)][CNT]++;
        store[map(i, j)][ECL] = store[map(i, j)][EIC] / store[map(i, j)][CNT];
    }

}
