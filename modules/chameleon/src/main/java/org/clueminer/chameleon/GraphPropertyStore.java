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

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.util.FastMath;

/**
 * Linear array storage of a triple - EIC, ECL and a counter. It has basically
 * size of n^2 because for storing leaves we need n*(n-1)/2 and for a complete
 * binary tree we need 2*{leaves count} -1
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
        double h = FastMath.log(2, capacity);
        //total number of nodes for storing a binary tree
        int n = (int) Math.ceil(FastMath.pow(2, h + 1) - 1);
        //we need similarities for newly created nodes (merged clusters)
        store = new double[triangleSize(n)][3];
        System.out.println("allocated gs " + store.length);
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

    /**
     * Directly set all values
     *
     * @param i
     * @param j
     * @param eic
     * @param ecl
     * @param cnt
     */
    public void set(int i, int j, double eic, double ecl, double cnt) {
        store[map(i, j)][EIC] = eic;
        store[map(i, j)][ECL] = ecl;
        store[map(i, j)][CNT] = cnt;
    }

    public void dump() {
        printFancy(2, 2);
    }

    public void printFancy(int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printFancy(new PrintWriter(System.out, true), format, w + 2);
    }

    public void printFancy(PrintWriter output, NumberFormat format, int width) {
        int colCnt = 3;
        String s;
        int padding;
        output.println();  // start on new line.
        for (int i = 0; i < store.length; i++) {
            //print row label
            s = String.valueOf(i);
            padding = Math.max(1, width - s.length() - 1);
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
            output.print(" |");
            for (int j = 0; j < colCnt; j++) {
                s = format.format(store[i][j]); // format the number
                padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        //footer
        for (int i = 0; i < width * (colCnt + 1); i++) {
            output.print('-');
        }
        output.println();
        for (int k = 0; k < width; k++) {
            output.print(' ');
        }
        for (int i = 0; i < colCnt; i++) {
            s = String.valueOf(i); // format the number
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();
    }

}
