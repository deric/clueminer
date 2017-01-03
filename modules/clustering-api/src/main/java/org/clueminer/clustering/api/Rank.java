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
package org.clueminer.clustering.api;

import java.util.HashMap;

/**
 * Measure relationship between rankings
 *
 * @author deric
 */
public interface Rank {

    /**
     * A unique identification of the method
     *
     * @return method identifier
     */
    String getName();

    /**
     * Compute correlation (or some other metric) that evaluates similarity
     * between rankings
     *
     * @param current a ranking
     * @param ref reference ranking
     * @param map reference id mapping (id clustering -> actual rank)
     * @return coefficient (typically between -1 and 1)
     */
    double correlation(Clustering[] current, Clustering[] ref, HashMap<Integer, Integer> map);
}
