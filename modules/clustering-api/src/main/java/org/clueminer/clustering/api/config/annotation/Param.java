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
package org.clueminer.clustering.api.config.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.clueminer.clustering.params.ParamType;

/**
 *
 * @author Tomas Barton
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD})
public @interface Param {

    /**
     * Human readable name of this parameter.
     *
     * @return a human readable name of this property
     */
    String name() default "";

    /**
     * Human readable description of this parameter.
     *
     * @return string description
     */
    String description() default "";

    /**
     * Whether this option is required.
     *
     * @return
     */
    boolean required() default false;

    /**
     * Service factory provider
     *
     * @return
     */
    String factory() default "";

    /**
     * Type of stored value, currently supported values are: double, string,
     * boolean, integer
     *
     * @return
     */
    ParamType type() default ParamType.NULL;

    double min() default Double.NaN;

    double max() default Double.NaN;
}
