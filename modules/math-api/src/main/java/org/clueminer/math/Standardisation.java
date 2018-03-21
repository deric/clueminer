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
package org.clueminer.math;

/**
 *
 * @author Tomas Barton
 */
public abstract class Standardisation {

    public abstract String getName();

    /**
     * Perform standardization of input data and return new array with adjusted
     * values
     *
     * @param data
     * @param m    number of rows
     * @param n    number of columns
     * @return standardized data
     */
    public abstract double[][] optimize(double[][] data, int m, int n);

    /**
     * Computes average (mean) value of the array
     *
     * @param array input data
     * @return
     */
    public double average(double[] array) {
        double total = 0.0;
        for (int i = 0; i < array.length; i++) {
            total += array[i];
        }
        return (total / array.length);
    }

}
