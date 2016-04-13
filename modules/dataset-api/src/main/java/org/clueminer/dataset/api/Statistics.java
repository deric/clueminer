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
package org.clueminer.dataset.api;

import java.io.Serializable;

/**
 * Interface for computing online (running) statistics for an attribute.
 *
 * @author Tomas Barton
 */
public interface Statistics extends Serializable, Cloneable {

    Object clone();

    void reset();

    /**
     * Update statistics with a new value
     *
     * @param value newly arrived value
     */
    void valueAdded(Object value);

    /**
     * A value was removed from a dataset
     *
     * @param value
     */
    void valueRemoved(Object value);

    /**
     * Array of statistics which are provided by the class
     *
     * @return
     */
    IStats[] provides();

    /**
     * Value of the statistics with given name. The name should be unique within
     * all statistics providers
     *
     * @param name
     * @return
     */
    double statistics(IStats name);

    /**
     * Returns the specified information for the whole data source.
     *
     * @param key Requested information.
     * @return The value for the specified key as value, or <i>NaN</i>
     * if the specified statistical value does not exist
     */
    double get(String key);
}
