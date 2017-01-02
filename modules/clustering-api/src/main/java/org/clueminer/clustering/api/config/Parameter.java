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
package org.clueminer.clustering.api.config;

import org.clueminer.clustering.params.ParamType;

/**
 * A parameter of an algorithm, that could be either used for influencing result
 * quality or performance of an algorithm.
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface Parameter<T> {

    /**
     * Used for interactive configuration to retrieve a human readable parameter
     * name.
     *
     * @return a <code>String</code> containing a human readable name
     */
    String getName();

    /**
     * Used for interactive configuration to retrieve a human readable parameter
     * description.
     *
     * @return a <code>String</code> containing a human readable description
     */
    String getDescription();

    /**
     * Returns the current property value.
     *
     * @return current property value
     */
    T getValue();

    /**
     * Sets the property value.
     *
     * @param value to set the property to
     */
    void setValue(T value);

    /**
     *
     * @return type of this parameter
     */
    ParamType getType();

    /**
     * case BOOLEAN: upperLimit[i] = 1; combinations *= 2;
     * logger.log(Level.INFO, "possible values: {0}", 2); break; * Factory for getting possible values
     *
     * @return String
     */
    String getFactory();

    /**
     * Min posible value (in case of ordinary variables)
     *
     * @return
     */
    double getMin();

    /**
     * Maximum values (if defined and make sense)
     *
     * @return
     */
    double getMax();

    /**
     *
     * @param min
     */
    void setMin(double min);

    /**
     *
     * @param max
     */
    void setMax(double max);

    /**
     * An exception is typically thrown in case that required parameter is
     * not given.
     *
     * @return true when parameter need to be specified
     */
    boolean isRequired();

    /**
     * Require parameter for each algorithm run?
     *
     * @param b
     */
    void setRequired(boolean b);
}
