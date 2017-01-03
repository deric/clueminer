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
package org.clueminer.eval.utils;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.row.DataItem;
import org.clueminer.math.Numeric;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Simple test for ensuring that all scores behaves correctly when sorting
 * values
 *
 * @author deric
 */
public class ScoreComparatorTest {

    private static final ScoreComparator subject = new ScoreComparator();
    private static final double delta = 1e-9;

    @Test
    public void testCompare() {
        double[] values = new double[]{15, 8, 2.3, 3.14, 155, 4};
        sortArray(values, 2.3, 155);

        values = new double[]{5, Double.NaN, 1, 8.3};
        sortNaN(values, 1, 8.3);

        //2* NaN
        values = new double[]{5, Double.NaN, Double.NaN, 1, 8.3};
        sortNaN(values, 1, 8.3);

        values = new double[]{5, Double.NEGATIVE_INFINITY, 1, 8.3};
        sortNinf(values, 1.0, 8.3);

        values = new double[]{5, Double.NEGATIVE_INFINITY, 1, 8.3, Double.NaN, 2};
        sortNinfNaN(values, Double.NaN, 8.3);
    }

    private void sortArray(double[] values, double min, double max) {
        Numeric ary[] = createArray(values);
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (InternalEvaluator e : eval) {
            subject.setEvaluator(e);
            Arrays.sort(ary, subject);
            if (e.isMaximized()) { // in fact this should be equal to Collections.reverseOrder()
                //first value is the best
                assertEquals(max, ary[0].getValue(), delta);
                //last is the worst
                assertEquals(min, ary[ary.length - 1].getValue(), delta);
            } else {
                assertEquals(min, ary[0].getValue(), delta);
                assertEquals(max, ary[ary.length - 1].getValue(), delta);
            }
        }
    }

    private Numeric[] createArray(double[] values) {
        //Dump.array(values, "test array");
        Numeric ary[] = new Numeric[values.length];
        for (int i = 0; i < ary.length; i++) {
            ary[i] = new DataItem(values[i]); //just wrapper around double
        }
        return ary;
    }

    private void sortNaN(double[] values, double min, double max) {
        Numeric ary[] = createArray(values);
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (InternalEvaluator e : eval) {
            subject.setEvaluator(e);
            System.out.println("testing " + e.getName() + " maximized: " + e.isMaximized());
            Arrays.sort(ary, subject);
            System.out.println(Arrays.toString(ary));
            if (e.isMaximized()) {
                //first value is the best
                assertEquals(max, ary[0].getValue(), delta);
                assertEquals(Double.NaN, ary[ary.length - 1].getValue(), delta);
            } else {
                assertEquals(min, ary[0].getValue(), delta);
                assertEquals(Double.NaN, ary[ary.length - 1].getValue(), delta);
            }
        }
    }

    private void sortNinf(double[] values, double min, double max) {
        Numeric ary[] = createArray(values);
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (InternalEvaluator e : eval) {
            subject.setEvaluator(e);
            System.out.println("testing " + e.getName() + " maximized: " + e.isMaximized());
            Arrays.sort(ary, subject);
            System.out.println(Arrays.toString(ary));
            if (e.isMaximized()) {
                //first value is the best
                assertEquals(max, ary[0].getValue(), delta);
                assertEquals(Double.NEGATIVE_INFINITY, ary[ary.length - 1].getValue(), delta);
            } else {
                assertEquals(Double.NEGATIVE_INFINITY, ary[0].getValue(), delta);
                assertEquals(max, ary[ary.length - 1].getValue(), delta);
            }
        }
    }

    private void sortNinfNaN(double[] values, double min, double max) {
        Numeric ary[] = createArray(values);
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (InternalEvaluator e : eval) {
            subject.setEvaluator(e);
            System.out.println("testing " + e.getName() + " maximized: " + e.isMaximized());
            Arrays.sort(ary, subject);
            System.out.println(Arrays.toString(ary));
            if (e.isMaximized()) {
                //first value is the best
                assertEquals(max, ary[0].getValue(), delta);
                //NaN is the worst
                assertEquals(min, ary[ary.length - 1].getValue(), delta);
            } else {
                assertEquals(Double.NEGATIVE_INFINITY, ary[0].getValue(), delta);
                assertEquals(min, ary[ary.length - 1].getValue(), delta);
            }
        }
    }
}
