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

import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Find nearest neighbors to the given query
 *
 * @author deric
 * @param <K>
 */
public interface KNNSearch<K extends Instance> extends NearestNeighborSearch<K> {

    /**
     * Search the k nearest neighbors to the query.
     *
     * @param q the query key.
     * @param k	the number of nearest neighbors to search for.
     * @return
     */
    Neighbor[] knn(K q, int k);

    /**
     * Search the k nearest neighbors to the query.
     *
     * @param q      the query key.
     * @param k	     the number of nearest neighbors to search for.
     * @param params
     * @return
     */
    Neighbor[] knn(K q, int k, Props params);

}
