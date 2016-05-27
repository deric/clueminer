/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.stats;

import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NumericalStatsTest {

    private static Dataset<? extends Instance> ds1;
    private static Dataset<? extends Instance> ds2;
    private static Dataset<? extends Instance> ds3;
    private static final double DELTA = 1e-9;

    @BeforeClass
    public static void setUpClass() {
        //second dataset { 7, 15, 36, 39, 40, 41}
        ds2 = new ArrayDataset<>(6, 1);
        ds2.attributeBuilder().create("x", "NUMERICAL");
        ds2.builder().create(new double[]{7});
        ds2.builder().create(new double[]{15});
        ds2.builder().create(new double[]{36});
        ds2.builder().create(new double[]{39});
        ds2.builder().create(new double[]{40});
        ds2.builder().create(new double[]{41});

        ds3 = ds3();
    }

    @Before
    public void setUp() {
        //we're modifying data during test
        ds1 = ds1();
    }

    private static Dataset<? extends Instance> ds1() {
        //first dataset
        Dataset<? extends Instance> ds = new ArrayDataset<>(11, 1);
        ds.attributeBuilder().create("x", "NUMERICAL");
        //6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49
        ds.builder().create(new double[]{6});
        ds.builder().create(new double[]{7});
        ds.builder().create(new double[]{15});
        ds.builder().create(new double[]{36});
        ds.builder().create(new double[]{39});
        ds.builder().create(new double[]{40});
        ds.builder().create(new double[]{41});
        ds.builder().create(new double[]{42});
        ds.builder().create(new double[]{43});
        ds.builder().create(new double[]{47});
        ds.builder().create(new double[]{49});
        return ds;
    }

    private static Dataset<? extends Instance> ds3() {
        //dataset {1.8, 2, 2.1, 2.4, 2.6, 2.9, 3}
        Dataset<? extends Instance> ds = new ArrayDataset<>(7, 1);
        ds.attributeBuilder().create("x", "NUMERICAL");
        ds.builder().create(new double[]{1.8});
        ds.builder().create(new double[]{2});
        ds.builder().create(new double[]{2.1});
        ds.builder().create(new double[]{2.4});
        ds.builder().create(new double[]{2.6});
        ds.builder().create(new double[]{2.9});
        ds.builder().create(new double[]{3});
        return ds;
    }

    @Test
    public void testClone() {
    }

    @Test
    public void testBasicStats() {
        Attribute attr = ds1.getAttribute(0);
        assertEquals(ds1.size(), attr.size());
        assertEquals(11, attr.size());
        assertEquals(6, attr.statistics(StatsNum.MIN), DELTA);
        assertEquals(49, attr.statistics(StatsNum.MAX), DELTA);
    }

    @Test
    public void testReset() {
    }

    @Test
    public void testRecalculate() {
        Attribute attr = ds1.getAttribute(0);
        assertEquals(6, attr.statistics(StatsNum.MIN), DELTA);
        assertEquals(49, attr.statistics(StatsNum.MAX), DELTA);
        attr.resetStats();
        assertEquals(49, attr.statistics(StatsNum.MAX), DELTA);
    }

    @Test
    public void testValueAdded() {
        Attribute attr = ds1.getAttribute(0);
        assertEquals(49, attr.statistics(StatsNum.MAX), DELTA);
        ds1.builder().create(new double[]{52});
        assertEquals(52, attr.statistics(StatsNum.MAX), DELTA);
    }

    @Test
    public void testQuartiles() {
        Attribute attr = ds1.getAttribute(0);
        assertEquals(15, attr.statistics(StatsNum.Q1), DELTA);
        assertEquals(43, attr.statistics(StatsNum.Q3), DELTA);
        attr = ds2.getAttribute(0);
        assertEquals(15, attr.statistics(StatsNum.Q1), DELTA);
        assertEquals(40, attr.statistics(StatsNum.Q3), DELTA);
    }

    /**
     * From examples at https://en.wikipedia.org/wiki/Quartile
     */
    @Test
    public void testMedian() {
        Attribute attr = ds1.getAttribute(0);
        assertEquals(40, attr.statistics(StatsNum.MEDIAN), DELTA);
        attr = ds2.getAttribute(0);
        assertEquals(6, attr.size());
        assertEquals(37.5, attr.statistics(StatsNum.MEDIAN), DELTA);
    }

    @Test
    public void testDS3() {
        Attribute attr = ds3.getAttribute(0);
        assertEquals(1.2, attr.statistics(StatsNum.RANGE), DELTA);
        assertEquals(2.0, attr.statistics(StatsNum.Q1), DELTA);
        assertEquals(2.4, attr.statistics(StatsNum.MEDIAN), DELTA);
        assertEquals(2.9, attr.statistics(StatsNum.Q3), DELTA);
        assertEquals(0.18367346938775508, attr.statistics(StatsNum.QCD), DELTA);
    }

}
