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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.util.FastMath;

/**
 * Linear array storage of a triple - EIC, ECL and a counter. It has basically
 * size of n^2 because for storing leaves we need n*(n-1)/2.
 *
 * We need space for n*n similarity matrix + similarities of binary tree with
 * height log2(n). Each inner node of the tree (new cluster) will compute a
 * distance to unmerged clusters. While distances between leaves forms a dense
 * matrix, distances between inner nodes are sparse.
 *
 * Should be safe to use up to dataset of size 98621
 *
 * @author deric
 */
public class GraphPropertyStore {

    //dimension of the matrix
    private final int n;
    private double defaultValue = 0.0;

    private final ExtProp[] store;
    private final Table<Integer, Integer, ExtProp> sparse;

    public GraphPropertyStore(int capacity) {
        this.n = capacity;
        //int nodes = innerTreeNodes(capacity);
        //we need similarities for newly created nodes (merged clusters)
        int simMatrixSize = triangleSize(capacity);
        store = new ExtProp[simMatrixSize];
        sparse = HashBasedTable.create();
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

    protected final int innerTreeNodes(int leaves) {
        //height of a binary tree with {capacity} nodes
        double h = FastMath.log(2, leaves);
        //total number of inner nodes of a binary tree
        return (int) Math.floor(FastMath.pow(2, h) - 1);
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

    /**
     * Set value either to dense matrix or to a sparse storage (for larger i, j)
     *
     * @param i
     * @param j
     * @param idx
     * @param value
     */
    private void set(int i, int j, ExtProp value) {
        if (i >= n || j >= n) {
            if (i < j) {
                /**
                 * swap variables, matrix is symmetrical, we work with lower
                 * triangular matrix
                 */
                int tmp = i;
                i = j;
                j = tmp;
            }
            sparse.put(i, j, value);
        } else {
            store[map(i, j)] = value;
        }
    }

    public ExtProp get(int i, int j) {
        if (i >= n || j >= n) {
            if (i < j) {
                /**
                 * swap variables, matrix is symmetrical, we work with lower
                 * triangular matrix
                 */
                int tmp = i;
                i = j;
                j = tmp;
            }
            //sparse storage
            if (sparse.contains(i, j)) {
                return sparse.get(i, j);
            } else {
                ExtProp p = new ExtProp();
                set(i, j, p);
                return p;
            }
        } else {
            ExtProp p;
            try {
                p = store[map(i, j)];
                if (p == null) {
                    p = new ExtProp();
                    store[map(i, j)] = p;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("was getting [" + i + ", " + j + "] -> " + map(i, j) + " length = " + store.length);
                throw ex;
            }
            return p;
        }
    }

    public double getEIC(int i, int j) {
        ExtProp r = get(i, j);
        return r.EIC;
    }

    public double getECL(int i, int j) {
        ExtProp r = get(i, j);
        return r.ECL;
    }

    /**
     * Counter value - number of edges that contributed to EIC weights sum
     *
     * @param i
     * @param j
     * @return
     */
    public double getCnt(int i, int j) {
        ExtProp r = get(i, j);
        return r.counter;
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
        ExtProp p = get(i, j);
        p.EIC += edgeWeight;
        p.counter += 1;
        p.ECL = p.EIC / p.counter;
        set(i, j, p);
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
        ExtProp p = get(i, j);
        p.EIC = eic;
        p.ECL = ecl;
        p.counter = (int) cnt;
        set(i, j, p);
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
        String s;
        int padding;
        ExtProp d;
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
            d = store[i];
            output.print(d.EIC + ", ");
            output.print(d.ECL + ", ");
            output.println(d.counter);
        }
        //footer
        for (int i = 0; i < width * (3 + 1); i++) {
            output.print('-');
        }
        output.println();
        for (int k = 0; k < width; k++) {
            output.print(' ');
        }
        String[] values = new String[]{"EIC", "ECL", "CNT"};
        for (String value : values) {
            s = value;
            padding = Math.max(1, width - s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++) {
                output.print(' ');
            }
            output.print(s);
        }
        output.println();

        output.println("== sparse (" + sparse.size() + "): ");

        for (Table.Cell<Integer, Integer, ExtProp> entry : sparse.cellSet()) {
            output.print(entry.getRowKey() + ", " + entry.getColumnKey() + ": ");
            d = entry.getValue();
            output.print("EIC= " + d.EIC + ", ");
            output.print("ECL= " + d.ECL + ", ");
            output.println("CNT= " + d.counter);
        }
    }

    public int getCapacity() {
        return store.length;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected class ExtProp {

        public double EIC, ECL;
        public int counter;

        public ExtProp() {
            EIC = ECL = counter = 0;
        }
    }

}
