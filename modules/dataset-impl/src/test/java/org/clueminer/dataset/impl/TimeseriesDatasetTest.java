/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.dataset.impl;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.row.TimeInstance;
import org.clueminer.types.TimePoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class TimeseriesDatasetTest {

    private static TimeseriesDataset<ContinuousInstance> dataset;
    private static final double DELTA = 1e-9;

    public TimeseriesDatasetTest() {
    }

    @Before
    public void setUp() {
        dataset = new TimeseriesDataset<>(5);
        TimePoint tp[] = new TimePointAttribute[6];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
        }
        dataset.setTimePoints(tp);
        InstanceBuilder builder = dataset.builder();
        double[] data;
        int size = 10;
        for (int i = 0; i < size; i++) {
            data = new double[tp.length];
            for (int j = 0; j < data.length; j++) {
                data[j] = Math.random();
            }
            builder.create(data);
        }
    }

    @Test
    public void testAttributeCount() {
    }

    /**
     * Test of getAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttribute_int() {
    }

    /**
     * Test of setAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttribute() {
    }

    /**
     * Test of setAttributes method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttributes() {
    }

    /**
     * Test of add method, of class TimeseriesDataset.
     */
    @Test
    public void testAdd() {
        int size = dataset.size();
        dataset.add(new TimeInstance(dataset.attributeCount()));
        assertEquals(size + 1, dataset.size());
    }

    /**
     * Test of addAll method, of class TimeseriesDataset.
     */
    @Test
    public void testAddAll() {
    }

    /**
     * Test of check method, of class TimeseriesDataset.
     */
    @Test
    public void testCheck() {
    }

    @Test
    public void testGetAttributes() {
        TimeseriesDataset<ContinuousInstance> test = new TimeseriesDataset(5);
        for (int i = 0; i < 15; i++) {
            test.attributeBuilder().create("attr " + i, "TIME");
        }
        int i = 0;
        for (Attribute attr : test.getAttributes().values()) {
            assertNotNull((i++) + "-th attribute should not be null", attr);
        }
    }

    @Test
    public void testCopyAttributes() {
    }

    @Test
    public void testGetTimePoints() {
    }

    @Test
    public void testGetTimePointsArray() {
        TimeseriesDataset<ContinuousInstance> test = new TimeseriesDataset(5);
        for (int i = 0; i < 15; i++) {
            test.attributeBuilder().create("attr " + i, "TIME");
        }
        double[] tp = test.getTimePointsArray();
        assertNotNull(tp);
    }

    @Test
    public void testSetTimePoints() {
    }

    /**
     * Test of crop method, of class TimeseriesDataset.
     */
    @Test
    public void testCrop() {
    }

    /**
     * Test of checkMinMax method, of class TimeseriesDataset.
     */
    @Test
    public void testCheckMinMax() {
        double[] data = new double[dataset.attributeCount()];
        for (int j = 0; j < data.length; j++) {
            data[j] = Math.random();
        }
        double max = Math.random() * 100000;
        data[0] = max;
        dataset.builder().create(data);
        assertEquals(max, dataset.getMax(), DELTA);
    }

    /**
     * Test of resetMinMax method, of class TimeseriesDataset.
     */
    @Test
    public void testResetMinMax() {
    }

    /**
     * Test of interpolate method, of class TimeseriesDataset.
     */
    @Test
    public void testInterpolate() {
    }

    /**
     * Test of getMin method, of class TimeseriesDataset.
     */
    @Test
    public void testGetMinMax() {
        double min = dataset.getMin();
        double max = dataset.getMax();
        assertEquals(true, max >= min);
    }

    /**
     * Test of equals method, of class TimeseriesDataset.
     */
    @Test
    public void testEquals() {
    }

    /**
     * Test of hashCode method, of class TimeseriesDataset.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of toString method, of class TimeseriesDataset.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of getClasses method, of class TimeseriesDataset.
     */
    @Test
    public void testGetClasses() {
        TimePoint tp[] = new TimePointAttribute[3];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
        }
        Dataset<? extends Instance> test = new TimeseriesDataset<>(3, (TimePointAttribute[]) tp);
        test.builder().build(new double[]{1, 2, 3}, "a");
        test.builder().build(new double[]{4, 5, 6}, "b");
        test.builder().build(new double[]{7, 8, 9}, "c");

        assertEquals(test.size(), 3);

        assertEquals(test.getClasses().size(), 3);

        assertEquals(test.classValue(0), "a");
        assertEquals(test.classValue(1), "b");
        assertEquals(test.classValue(2), "c");
    }

    /**
     * Test of classIndex method, of class TimeseriesDataset.
     */
    @Test
    public void testClassIndex() {
    }

    /**
     * Test of classValue method, of class TimeseriesDataset.
     */
    @Test
    public void testClassValue() {
    }

    /**
     * Test of builder method, of class TimeseriesDataset.
     */
    @Test
    public void testBuilder() {
    }

    /**
     * Test of attributeBuilder method, of class TimeseriesDataset.
     */
    @Test
    public void testAttributeBuilder() {
    }

    /**
     * Test of copy method, of class TimeseriesDataset.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of instance method, of class TimeseriesDataset.
     */
    @Test
    public void testInstance() {
        assertNotNull(dataset.instance(0));
        assertNotNull(dataset.instance(1));
        assertNotNull(dataset.instance(dataset.size() - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInstanceException() {
        dataset.instance(-1);
    }

    /**
     * Test of getRandom method, of class TimeseriesDataset.
     */
    @Test
    public void testGetRandom() {
    }

    @Test
    public void testGet() {
        TimePoint tp[] = new TimePointAttribute[3];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
        }
        Dataset<? extends Instance> test = new TimeseriesDataset<>(3, (TimePointAttribute[]) tp);
        double[][] data = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        //test matrix-like approach to accessing data
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                test.set(i, j, data[i][j]);
                assertEquals(data[i][j], test.get(i, j), DELTA);
            }
        }
    }

    @Test
    public void testAddAttribute() {
        Dataset<? extends Instance> test = new TimeseriesDataset<>(3);
        int max = 3;
        for (int i = 0; i < max; i++) {
            test.addAttribute(new TimePointAttribute(i, i + 100, Math.pow(i, 2)));
        }

    }

    /**
     * Test of getAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributeValue_Attribute_int() {
    }

    /**
     * Test of getAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributeValue_int_int() {
    }

    /**
     * Test of setAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttributeValue() {
    }

    /**
     * Test of getPlotter method, of class TimeseriesDataset.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of getAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttribute_String() {
    }

    /**
     * Test of duplicate method, of class TimeseriesDataset.
     */
    @Test
    public void testDuplicate() {
    }

    @Test
    public void testInstanceIndex() {
        Instance inst = dataset.get(0);
        assertEquals(0, inst.getIndex());
        int index = dataset.size() - 1;
        inst = dataset.get(index);
        assertEquals(index, inst.getIndex());
    }
}
