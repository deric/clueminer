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
package org.clueminer.dataset.api;

/**
 * Same as {@link org.clueminer.math.Standardisation} but operates on top of
 * Datasets
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface DataStandardization<E extends Instance> {

    /**
     *
     * @return name of the method
     */
    String getName();

    /**
     * Perform standardization of input data and return new Matrix with adjusted
     * values
     *
     * @param dataset
     * @return
     */
    Dataset<E> optimize(Dataset<E> dataset);

}
