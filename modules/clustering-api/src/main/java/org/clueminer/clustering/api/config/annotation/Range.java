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
package org.clueminer.clustering.api.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.clueminer.clustering.api.config.InRangeValidator;

/**
 * Annotation for restricting property to a certain range
 *
 * @author Tomas Barton
 */
@Constraint(validatedBy = InRangeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {

    /**
     * The lower bound of the range.
     *
     * @return double value representing the lower bound
     */
    double from();

    /**
     * The upper bound of the range.
     *
     * @return double value representing the upper bound
     */
    double to();

    Class<?>[] groups() default {};

    String message() default "property not in range <{from},{to}>";

}
