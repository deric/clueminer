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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E> row data type
 */
public interface SeedSelection<E extends Instance> {

    /**
     * Unique method identification
     *
     * @return name of the method
     */
    String getName();

    /**
     * Select k indexes of medoids from given Dataset (medoids are existing
     * instances in the dataset). Selected points represent initial prototypes
     * of final clusters.
     *
     * @param dataset from which we select points (instances)
     * @param params  configuration parameters such as number of points to be selected
     * @return
     */
    E[] selectPrototypes(Dataset<E> dataset, Props params);

}
