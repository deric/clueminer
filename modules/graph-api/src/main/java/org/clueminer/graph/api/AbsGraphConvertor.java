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
package org.clueminer.graph.api;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 *
 * @author deric
 */
public abstract class AbsGraphConvertor<E extends Instance> implements GraphConvertor<E> {

    protected final double EPS = 1e-6;
    protected Distance dm;

    /**
     * How to convert distance between points (default: inverse value)
     */
    public static final String DIST_TO_EDGE = "dist_to_edge";

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    @Override
    public Distance getDistanceMeasure() {
        return dm;
    }

    public double convertDistance(double dist, DIST2EDGE methd) {
        if (dist < EPS) {
            dist = EPS;
        }
        switch (methd) {
            case INVERSE:
                return 1.0 / dist;
            case LOG2:
                return Math.log(dist);
            case DISTANCE:
                return dist;
            default:
                throw new RuntimeException("distance conversion method " + methd + " is not supported");
        }
    }

}
