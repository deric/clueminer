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

import org.clueminer.math.IntMatrix;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class IntegerMatrixTest {

    public IntegerMatrixTest() {
    }

    @Test
    public void testGet() {
        IntMatrix A = new IntegerMatrix(2, 2, 5);
        assertEquals(5, A.get(0, 0));
        assertEquals(5, A.get(1, 0));
        assertEquals(5, A.get(0, 1));
        assertEquals(5, A.get(1, 1));
    }

    @Test
    public void testSet() {
        IntMatrix A = new IntegerMatrix(2, 2);
        assertEquals(0, A.get(0, 0));
        A.set(0, 0, 8);
        assertEquals(8, A.get(0, 0));
    }

    @Test
    public void testRowsCount() {
        IntMatrix A = new IntegerMatrix(2, 5);
        assertEquals(2, A.rowsCount());
    }

    @Test
    public void testColumnsCount() {
        IntMatrix A = new IntegerMatrix(2, 5);
        assertEquals(5, A.columnsCount());
    }

}
