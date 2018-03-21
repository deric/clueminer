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
package org.clueminer.projection;

import java.util.Comparator;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;

/**
 *
 * @author deric
 */
public class DistanceComparator implements Comparator<DataPoint> {

    DataPoint refItem;
    Distance dist;

    DistanceComparator(DataPoint refItem) {
        this.refItem = refItem;
        this.dist = new EuclideanDistance();
    }

    DistanceComparator(DataPoint refItem, Distance dist) {
        this.refItem = refItem;
        this.dist = dist;
    }

    @Override
    public int compare(DataPoint o1, DataPoint o2) {
        return dist.d(o1.data, refItem.data) < dist.d(o2.data, refItem.data) ? -1
               : (dist.d(o1.data, refItem.data) > dist.d(o2.data, refItem.data) ? 1 : 0);
    }
}
