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
