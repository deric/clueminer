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
package org.clueminer.utils;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DMatrixTest {

    private static double[][] data = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static DMatrix subject;
    private static double DELTA = 1e-9;

    public DMatrixTest() {
        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        subject = new DMatrix(dataset);
    }

    @Test
    public void testSetup() {
        subject.print(2, 0);
    }


    @Test
    public void testRowsCount() {
        assertEquals(2, subject.rowsCount());
    }

    @Test
    public void testColumnsCount() {
        assertEquals(5, subject.columnsCount());
    }

    @Test
    public void testGet() {
        assertEquals(1, subject.get(0, 0), DELTA);
    }


}
