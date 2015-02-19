/*
 * Copyright (C) 2015 clueminer.org
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

import java.util.Comparator;

/**
 *
 * @author Tomas Barton
 */
public class DmComparator implements Comparator<Double> {

    private DistanceMeasure dm;
    private double delta = 1e-9;

    public DmComparator(DistanceMeasure dm) {
        this.dm = dm;
    }

    @Override
    public int compare(Double d1, Double d2) {
        double diff = d1 - d2;
        //same or within tolerance
        if (Math.abs(diff) < delta) {
            return 0;
        } else {
            if (dm.compare(d1, d2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

}
