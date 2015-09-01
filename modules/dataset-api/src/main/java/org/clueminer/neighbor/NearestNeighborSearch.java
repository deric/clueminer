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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 *
 * @author deric
 * @param <K> the key
 */
public interface NearestNeighborSearch<K extends Instance> {

    /**
     * Unique algorithm identifier
     *
     * @return the name of algorithm implementation
     */
    String getName();

    /**
     * Set current working dataset
     *
     * @param dataset
     */
    void setDataset(Dataset<K> dataset);

    /**
     * Set distance measure
     *
     * @param dist
     */
    void setDistanceMeasure(Distance dist);

    /**
     * Find closest data point
     *
     * @param q
     * @return
     */
    Neighbor<K> nearest(K q);

    /**
     * When set to true instance itself will be included in results
     *
     * @param identicalExcluded
     */
    void setIdenticalExcluded(boolean identicalExcluded);

    /**
     * Default: true
     *
     * @return whether to exclude instance itself in results
     */
    boolean isIdenticalExcluded();
}
