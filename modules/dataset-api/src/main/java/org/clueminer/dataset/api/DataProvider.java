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
 *
 * @author deric
 * @param <E>
 */
public interface DataProvider<E extends Instance> extends Iterable<Dataset<E>> {

    /**
     *
     * @return names of available datasets
     */
    String[] getDatasetNames();

    /**
     * Retrieve specific dataset
     *
     * @param name
     * @return
     */
    Dataset<E> getDataset(String name);

    /**
     *
     * @return first dataset in collection
     */
    Dataset<E> first();

    /**
     *
     * @return datasets count
     */
    int count();

    /**
     * Check if dataset with given name is contained in this collection
     *
     * @param name
     * @return
     */
    boolean hasDataset(String name);

}
