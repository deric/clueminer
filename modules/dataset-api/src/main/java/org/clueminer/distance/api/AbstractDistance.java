/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.distance.api;

import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractDistance implements Distance {

    private static final long serialVersionUID = -4166447737887574607L;

    @Override
    public abstract String getName();

    /**
     * Return TRUE if x is better than y
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean compare(double x, double y) {
        return x < y;
    }

    /**
     * Minimal value of metric
     *
     * @return
     */
    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return Double.POSITIVE_INFINITY;
    }

    protected void checkInput(Vector<Double> x, Vector<Double> y) {
        if (x == null || y == null) {
            throw new ArithmeticException("Can't compute distance for null vector. x = " + x + ", y = " + y);
        }
        if (x.size() != y.size()) {
            throw new ArithmeticException("Both instances should contain the same number of values! x size: " + x.size() + " != y size: " + y.size());
        }
    }

    protected void checkInput(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new ArithmeticException("Both instances should contain the same number of values! x size: " + x.length + " != y size: " + y.length);
        }
    }
}
