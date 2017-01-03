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
package org.clueminer.distance;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Vector;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HammingDistanceTest {

    private static final HammingDistance subject = new HammingDistance();
    private static Instance a, b;
    private static final double delta = 1e-9;

    public HammingDistanceTest() {
    }

    @Before
    public void setUp() {
        a = new DoubleArrayDataRow(new double[]{0, 0});
        b = new DoubleArrayDataRow(new double[]{3, 4});
    }

    @Test
    public void testMeasure_Vector_Vector() {
        Vector x = new DoubleArrayDataRow(new double[]{0, 0});
        Vector y = new DoubleArrayDataRow(new double[]{1.0, 1.0});
        assertEquals(2.0, subject.measure(x, y), delta);
    }

    @Test
    public void testMeasureZeroDistance() {
        double dist = subject.measure(new DoubleArrayDataRow(new double[]{0, 0, 0}), new DoubleArrayDataRow(new double[]{0, 0, 0}));
        assertEquals(0.0, dist, delta);
    }

    @Test
    public void testIsSymmetric() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        double xy = subject.measure(x, y);
        double yx = subject.measure(y, x);
        assertEquals(subject.isSymmetric(), xy == yx);
    }

    @Test
    public void testIsSubadditive() {
        Vector x = new DoubleArrayDataRow(new double[]{1, 2, 3});
        Vector y = new DoubleArrayDataRow(new double[]{2, 4, 6});
        Vector z = new DoubleArrayDataRow(new double[]{3, 6, 9});

        double xy = subject.measure(x, y);
        double yz = subject.measure(y, z);
        double xz = subject.measure(x, z);

        assertEquals(subject.isSubadditive(), xz <= (xy + yz));
    }
;
}
