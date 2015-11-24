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
