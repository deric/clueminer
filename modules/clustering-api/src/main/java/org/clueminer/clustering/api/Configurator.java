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
package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Interface for estimating (or just guessing) parameter values for algorithms.
 *
 * @author deric
 * @param <E>
 */
public interface Configurator<E extends Instance> {

    /**
     * Configure algorithm for single run on given dataset by setting up
     * parameters in <code>params</code>. Params is a key-value store, that can
     * contain value of any type.
     *
     * @param dataset
     * @param params
     */
    void configure(Dataset<E> dataset, Props params);

    /**
     * Compute expected approximate algorithm complexity based on input data
     * size
     *
     * @param dataset
     * @param params
     * @return expected running time in ms
     */
    double estimateRunTime(Dataset<E> dataset, Props params);

}
