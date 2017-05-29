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
package org.clueminer.projection;

import static java.lang.Math.min;

/**
 *
 * @author deric
 */
public class DataPoint {

    int index;
    double[] data;
    int dim;

    public DataPoint() {
        dim = 1;
        index = -1;
    }

    public DataPoint(int D, int ind, double[] x) {
        dim = D;
        index = ind;
        data = x.clone();
    }

    @Override
    public String toString() {
        String xStr = "";
        for (int i = 0; i < min(20, data.length); i++) {
            xStr += data[i] + ", ";
        }
        return "DataPoint (index=" + index + ", Dim=" + dim + ", point=" + xStr + ")";
    }

    public int index() {
        return index;
    }

    int dimensionality() {
        return dim;
    }

    double x(int d) {
        return data[d];
    }
}
