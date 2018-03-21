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
package org.clueminer.colors;

import java.awt.Color;

/**
 *
 * @author Tomas Barton
 */
public class ColorTable {

    /**
     * Generates a series of colors such that the
     * distribution of the colors is (fairly) evenly spaced
     * throughout the color spectrum. This is especially
     * useful for generating unique color codes to be used
     * in a legend or on a graph.
     *
     * @param numColors the number of colors to generate
     * @return an array of Color objects representing the
     *         colors in the table
     */
    public Color[] createColorCodeTable(int numColors) {
        Color[] table = new Color[numColors];

        if (numColors == 1) {
            // Special case for only one color
            table[0] = Color.red;
        } else {
            float hueMax = (float) 0.85;
            float sat = (float) 0.8;

            for (int i = 0; i < numColors; i++) {
                float hue = hueMax * i / (numColors - 1);

                // Here we interleave light colors and dark colors
                // to get a wider distribution of colors.
                if (i % 2 == 0) {
                    table[i] = Color.getHSBColor(hue, sat, (float) 0.9);
                } else {
                    table[i] = Color.getHSBColor(hue, sat, (float) 0.7);
                }
            }
        }

        return table;
    }

}
