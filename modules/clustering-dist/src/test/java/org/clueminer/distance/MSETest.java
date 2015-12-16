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
package org.clueminer.distance;

import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MSETest {

    private static final MSE TEST = new MSE();
    private static final double DELTA = 1e-9;

    public MSETest() {

    }

    @Test
    public void testMeasure_Vector_Vector() {
        Vector x = new DoubleArrayDataRow(new double[]{0, 0});
        Vector y = new DoubleArrayDataRow(new double[]{1.0, 1.0});
        assertEquals(-1.0, TEST.measure(x, y), DELTA);
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = TEST.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, DELTA);
    }

    @Test
    public void testMeasure_3args() {
    }

    @Test
    public void testMeasure_doubleArr_doubleArr() {
    }

    @Test
    public void testIsSubadditive() {
    }

    @Test
    public void testIsIndiscernible() {
    }

}
