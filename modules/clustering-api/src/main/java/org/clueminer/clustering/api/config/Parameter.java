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
package org.clueminer.clustering.api.config;

import java.lang.reflect.InvocationTargetException;
import org.clueminer.clustering.params.ParamType;
import org.clueminer.utils.ServiceFactory;

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
     * Typically used for String attributes
     *
     * @return whether factory for given attribute exists
     */
    boolean hasFactory();

    /**
     *
     * @return String
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    ServiceFactory getFactory() throws ClassNotFoundException, NoSuchMethodException,
                                       IllegalAccessException, IllegalArgumentException,
                                       InvocationTargetException;

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
