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

import java.awt.Color;
import java.io.Serializable;

/**
 * Should be used for generating color schemes and colors for nice
 * visualizations, generated colors are expected to be as different as possible.
 *
 * @author Tomas Barton
 */
public interface ColorGenerator extends Serializable {

    /**
     * Name is used in GUI components as a human readable identifier.
     *
     * @return unique identifier
     */
    String getName();

    Color next();

    /**
     * Generate color based on the previous color
     *
     * @param base - color on which will be the result based
     * @return
     */
    Color next(Color base);

    /**
     * Go back to initial color (if generator is not purely random)
     */
    void reset();
}
