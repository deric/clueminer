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

import java.util.HashSet;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SymmetricMatrixDiagTest {

    private SymmetricMatrixDiag A, B, C;
    private static final double eps = 1e-9;

    @Before
    public void setUp() {
        A = new SymmetricMatrixDiag(3, 3);
        B = new SymmetricMatrixDiag(5, 5);
        C = new SymmetricMatrixDiag(10, 10);
    }

    /**
     * Test of rowsCount method, of class SymmetricMatrix.
     */
    @Test
    public void testRowsCount() {
        assertEquals(3, A.rowsCount());
        assertEquals(5, B.rowsCount());
        assertEquals(10, C.rowsCount());
    }

    /**
     * Test of columnsCount method, of class SymmetricMatrix.
     */
    @Test
    public void testColumnsCount() {
        assertEquals(3, A.columnsCount());
        assertEquals(5, B.columnsCount());
        assertEquals(10, C.columnsCount());
    }

    @Test
    public void testGetArray() {
        double[][] m = {{1, 0, 0}, {8, 2, 0}, {5, 4, 3}};
        A = new SymmetricMatrixDiag(3);
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j <= i; j++) {
                A.set(i, j, m[i][j]);
            }
        }
        A.print(2, 1);
        double[][] copy = A.getArray();
        Dump.matrix(copy, "copy", 3);

        int k = 1;
        for (int i = 0; i < m.length; i++) {
            //incremental numbers on diagonal
            assertEquals(A.get(i, i), k++, eps);
        }
    }

    @Test
    public void testMapping() {
        int n = 10;
        HashSet<Integer> hash = new HashSet<>(n * n);
        int mapped;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                mapped = C.map(i, j);
                if (hash.contains(mapped)) {
                    System.out.println("colision for (" + i + ", " + j + ") -> " + mapped);
                }
                assertEquals(false, hash.contains(mapped));
                hash.add(mapped);
            }
        }
        assertEquals(C.triangleSize(n), hash.size());
    }

    @Test
    public void testPlus() {
        int n = 4;
        //initialize matrix
        A = SymmetricMatrixDiag.random(n);
        Matrix X = A.plus(A);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                //should be 2 * A
                assertEquals(2 * A.get(i, j), X.get(i, j), eps);
            }
        }
    }

    @Test
    public void testTimes() {
        int n = 4;
        //initialize matrix
        A = SymmetricMatrixDiag.random(n);
        Matrix X = A.times(3.0);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //should be 2 * A
                assertEquals(3 * A.get(i, j), X.get(i, j), eps);
            }
        }
    }

    @Test
    public void testSetDiagonal() {
        int n = 5;
        Matrix M = new SymmetricMatrixDiag(n);
        M.setDiagonal(10);

        for (int i = 0; i < n; i++) {
            assertEquals(10.0, M.get(i, i), eps);
        }
    }

}
