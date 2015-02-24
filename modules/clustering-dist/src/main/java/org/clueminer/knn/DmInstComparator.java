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
package org.clueminer.knn;

import java.util.Comparator;
import java.util.Map;
import org.clueminer.distance.api.DistanceMeasure;

/**
 * Comparator for sorting map by values (default are keys)
 *
 * @author Tomas Barton
 * @param <K>
 * @param <V>
 */
public class DmInstComparator<K extends Comparable<? super K>, V extends Double> implements Comparator<Map.Entry<K, V>> {

    private DistanceMeasure dm;
    private double delta = 1e-9;

    public DmInstComparator(DistanceMeasure dm) {
        this.dm = dm;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public DistanceMeasure getMeasure() {
        return dm;
    }

    public void setMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

    @Override
    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        double d1 = o1.getValue();
        double d2 = o2.getValue();
        double diff = d1 - d2;
        //same or within tolerance
        if (Math.abs(diff) < delta) {
            return 0;
        } else {
            if (dm.compare(d1, d2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
