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
package org.clueminer.dataset.api;

import java.util.Iterator;
import org.clueminer.math.Interpolator;

/**
 * Represent data row that has continuous character (a value can be obtained for
 * any point between begin and end)
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface ContinuousInstance<E extends Number> extends Instance<E> {

    double valueAt(double x);

    double valueAt(double x, Interpolator interpolator);

    double getMin();

    double getMax();

    double getStdDev();

    /**
     * Crop given Instance to a smaller subset that should returned as a deep
     * copy.
     *
     * @param begin first index
     * @param end   last index that is included
     * @return
     */
    ContinuousInstance crop(int begin, int end);

    /**
     * Normalize data to given time point at index
     *
     * @param index
     */
    void normalize(int index);

    long getStartTime();

    @Override
    ContinuousInstance copy();

    /**
     * Returns an iterator over all statistics objects available for this type
     * of instance. Additional statistics can be registered via
     * {@link #registerStatistics(Statistics)}.
     *
     * @return
     */
    Iterator<Statistics> getAllStatistics();

    /**
     * Registers the instance statistics.
     *
     * @param statistics
     */
    void registerStatistics(Statistics statistics);

    /**
     * Return value of precomputed statistics, which should be on changes in
     * dataset updated
     *
     * @param name
     * @return
     */
    double statistics(Stats name);

    /**
     * Triggered when a new value is added to the instance. The type of a value
     * should be determined by the Attribute itself
     *
     *
     * @param value
     */
    void updateStatistics(double value);

    /**
     * Reset all registered statistics
     *
     */
    void resetStatistics();

}
