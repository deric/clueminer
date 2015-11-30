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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 * @param <E>
 */
public interface Distribution<E extends Instance> {

    void datasetChanged(Dataset<E> dataset);

    void clear();

    /**
     * Analyze data point
     *
     * @param value
     */
    void sample(double value);

    /**
     * Each bin contains number of samples in its range
     *
     * @return
     */
    int[] getBins();

    /**
     * Histogram range
     *
     * @return
     */
    int[] binsRange();

    /**
     * Number of analyzed samples
     *
     * @return
     */
    int getNumSamples();

    /**
     * Number of histogram bins
     *
     * @return
     */
    int getNumBins();

    /**
     * Histogram value at given value
     *
     * @param value
     * @return number of occurrences in bin containing this value
     */
    int hist(double value);

    /**
     * Range for each bin
     *
     * @return
     */
    double getStep();
}
