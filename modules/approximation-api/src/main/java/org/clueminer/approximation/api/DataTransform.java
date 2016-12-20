/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.approximation.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Generic interface for transforming data structure into different
 * representation
 *
 * @author Tomas Barton
 * @param <I> input row type
 * @param <O> output row type
 */
public interface DataTransform<I extends Instance, O extends Instance> {

    String getName();

    /**
     * Creates a discrete dataset from dataset with continuous values.
     * Transforms something into something else (run dimensionality reduction,
     * outliers detection, etc.)
     *
     * @param dataset
     * @param output
     * @param ph
     */
    void analyze(Dataset<I> dataset, Dataset<O> output, ProgressHandle ph);

    /**
     * Creates preferred data structure for storing results of this
     * transformation
     *
     * @param input input dataset, usually we use number of instances or
     *              dimensionality to optimize output storage
     * @return dataset for storing results
     */
    Dataset<O> createDefaultOutput(Dataset<I> input);
}
