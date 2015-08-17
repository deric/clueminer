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
package org.clueminer.clustering.api;

import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Reduce multiple clusterings into one resulting (consensus) clustering
 *
 * @author deric
 * @param <T>
 */
public interface Consensus<T extends Instance> {

    /**
     * Unique method identification
     *
     * @return name of the consensus method
     */
    String getName();

    /**
     * Integrate multiple clustering solutions into one
     *
     * @param clusts
     * @param alg
     * @param cg
     * @param props
     * @return
     */
    Clustering<? extends Cluster<? super T>> reduce(Clustering[] clusts, AbstractClusteringAlgorithm alg, ColorGenerator cg, Props props);
}
