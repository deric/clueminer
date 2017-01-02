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
package org.clueminer.clustering.api.dendrogram;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public interface ColorScheme {

    /**
     * Create color pallete which is afterwards used for generating colors
     *
     * @param color1
     * @param color2
     * @return
     */
    BufferedImage createGradientImage(Color color1, Color color2);

    /**
     * Generates color for given double value
     *
     * @param value
     * @param dendroData
     * @return
     */
    Color getColor(double value, DendrogramMapping dendroData);

    /**
     * Generate color for given value
     *
     * @param value
     * @param min
     * @param mid
     * @param max
     * @return
     */
    Color getColor(double value, double min, double mid, double max);

    /**
     * Generate requested color with alpha channel. Must be supported by canvas
     * (ARGB)
     *
     * @param value
     * @param min
     * @param mid
     * @param max
     * @param alpha value between 0 a 255
     * @return color with alpha (0-255)
     */
    Color getColor(double value, double min, double mid, double max, int alpha);

    boolean isUseDoubleGradient();

    /**
     * Whether to use gradient with 3 colors or just with 2
     *
     * @param useDoubleGradient using 3 colors when true
     */
    void setUseDoubleGradient(boolean useDoubleGradient);
}
