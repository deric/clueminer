/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.neighbor;

/**
 *
 * @author deric
 * @param <K>
 */
public class Neighbor<K> implements Comparable<Neighbor<K>> {

    /**
     * The key of neighbor.
     */
    public K key;

    /**
     * The index of neighbor object in the dataset.
     */
    public int index;

    /**
     * The distance between the query and the neighbor.
     */
    public double distance;

    public Neighbor(K key, int index, double distance) {
        this.key = key;
        this.index = index;
        this.distance = distance;
    }

    @Override
    public int compareTo(Neighbor<K> o) {
        return (int) Math.signum(distance - o.distance);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("key: ").append(key).append("[").append(index).append("] = ")
                .append(distance);

        return sb.toString();
    }

}
