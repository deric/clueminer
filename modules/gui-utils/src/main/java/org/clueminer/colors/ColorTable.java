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
