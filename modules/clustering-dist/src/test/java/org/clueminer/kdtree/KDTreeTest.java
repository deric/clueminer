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
package org.clueminer.kdtree;

import java.util.List;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author MSL
 */
public class KDTreeTest {

    static java.util.Random rand = new java.util.Random();

    static Vector<Double> makeSample(int dims) {
        DoubleArrayDataRow rv = new DoubleArrayDataRow(dims);
        for (int j = 0; j < dims; ++j) {
            rv.set(j, rand.nextDouble());
        }
        return rv;
    }

    @Test
    public void testNearestNeighborList() {
        NearestNeighborList<String> nnl = new NearestNeighborList<>(3);
        nnl.insert("A", 3.0);
        nnl.insert("B", 2.0);
        nnl.insert("D", 0.0);
        nnl.insert("C", 1.0);

        Assert.assertEquals(2.0, nnl.getMaxPriority(), 0.1);
        Assert.assertEquals("B", nnl.getHighest());
        Assert.assertEquals("B", nnl.removeHighest());
        Assert.assertEquals("C", nnl.removeHighest());
        Assert.assertEquals("D", nnl.removeHighest());
    }

    @Test
    public void testNearestNeighbor() throws KDException {
        int dims = 3;
        int samples = 300;
        KDTree<Integer> kt = new KDTree<>(dims);
        Vector<Double> targ = makeSample(dims);

        int min_index = 0;
        Distance dst = EuclideanDistance.getInstance();
        double min_value = Double.MAX_VALUE;
        for (int i = 0; i < samples; ++i) {
            Vector<Double> keys = makeSample(dims);
            kt.insert(keys, i);

            /*
             for the purposes of test, we want the nearest EVEN-NUMBERED point
             */
            if ((i % 2) == 0) {
                double dist = dst.measure(targ, keys);
                if (dist < min_value) {
                    min_value = dist;
                    min_index = i;
                }
            }
        }

        List<Integer> nbrs = kt.nearest(targ, 1, new Checker<Integer>() {
            public boolean usable(Integer v) {
                return (v % 2) == 0;
            }
        });

        Assert.assertEquals(1, nbrs.size());
        if (nbrs.size() == 1) {
            Assert.assertEquals(min_index, nbrs.get(0).intValue());
        }
    }

    @Test
    public void testRange() throws KDException {
        int dims = 2;
        KDTree<Object> kt = new KDTree<>(dims);
        Vector<Double> p0 = new DoubleArrayDataRow(new double[]{0.5, 0.5});
        Vector<Double> p1 = new DoubleArrayDataRow(new double[]{0.65, 0.5});
        Vector<Double> p2 = new DoubleArrayDataRow(new double[]{0.75, 0.5});

        kt.insert(p0, new Object());
        kt.insert(p1, new Object());
        kt.insert(p2, new Object());

        double[] lower = {0.25, 0.3};
        double[] upper = {0.7, 0.6};

        List<Object> rv = kt.range(lower, upper);
        Assert.assertEquals(2, rv.size());

        kt.delete(p1);
        rv = kt.range(lower, upper);
        Assert.assertEquals(1, rv.size());
    }

    @Test
    public void testSearch() throws KDException {
        int dims = 3;
        int samples = 300;
        KDTree<Object> kt = new KDTree<>(dims);
        Vector<Double> targ = makeSample(dims);
        Object treasure = new Object();
        kt.insert(targ, treasure);

        for (int i = 0; i < samples; ++i) {
            Vector<Double> keys = makeSample(dims);
            kt.insert(keys, i);
        }

        Object found = kt.search(targ);
        Assert.assertSame(treasure, found);

        kt.delete(targ);
        found = kt.search(targ);
        Assert.assertNull(found);

    }

    @Test
    public void testDelete() throws KDException {
        int dims = 3;
        KDTree<Object> kt = new KDTree<>(dims);
        Vector<Double> targ = makeSample(dims);
        kt.insert(targ, new Object());
        kt.delete(targ);
        try {
            kt.delete(targ);
            Assert.assertFalse(true);
        } catch (KeyMissingException e) {
            // supposed to be here
        }
        kt.delete(targ, true);
        Assert.assertEquals(0, kt.size());
    }

    @Test
    public void testEditing() throws KDException {
        int dims = 3;
        KDTree<Object> kt = new KDTree<>(dims);
        Vector<Double> targ = makeSample(dims);

        Object p1 = "p1";
        Object p2 = "p2";
        kt.insert(targ, p1);
        try {
            kt.insert(targ, p2);
            Assert.assertFalse(true);
        } catch (KeyDuplicateException e) {
            // supposed to be here
        }

        kt.edit(targ, new Editor.OptionalInserter<>(p2));
        Object found = kt.search(targ);
        Assert.assertSame(p1, found);
        kt.edit(targ, new Editor.Replacer<>(p2));
        found = kt.search(targ);
        Assert.assertSame(p2, found);
    }

}
