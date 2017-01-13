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
package org.clueminer.gui;

import java.awt.Color;

/**
 *
 * @author Tomas Barton
 */
public interface ColorPalette {

    void setRange(double min, double max);

    Color getColor(double value);

    /**
     *
     * @return min value in interval
     */
    double getMin();

    /**
     *
     * @return max value in the displayed interval
     */
    double getMax();

    /**
     *
     * @return middle value in the displayed interval
     */
    double getMid();

    /**
     * Finds complementary color that can be used as a text color for drawing on
     * given background
     *
     * @param bg
     * @return
     */
    Color complementary(Color bg);

}
