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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * A way of compressing data into smaller number of dimensions.
 *
 * @author deric
 * @param <E> row data type
 */
public interface Projection<E extends Instance> {

    /**
     * An unique method name
     *
     * @return method identifier
     */
    String getName();

    /**
     * Compute projection into numDims
     *
     * @param dataset    input data
     * @param targetDims
     */
    void initialize(Dataset<E> dataset, int targetDims);

    /**
     * Transform N-dimensional vector into precomputed number of dimensions
     *
     * @param instance
     * @return vector in target space
     */
    double[] transform(E instance);
}
