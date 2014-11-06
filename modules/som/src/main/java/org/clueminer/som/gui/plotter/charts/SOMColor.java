package org.clueminer.som.gui.plotter.charts;

import java.awt.Color;

import com.rapidminer.gui.plotter.ColorProvider;

/**
 * Provides colors for Self-Organizing Map. The colors are drawn
 * from a specified colormap. The default colormap schema is blueAndred.
 *
 * @author Jan Motl
 */
public class SOMColor {

    private String colorSchema = "blueAndRed";	// the default color schema to use

    /**
     * Class constructor.
     * The supported color schemas are:
     * Blue & Red
     * Black & White
     */
    public SOMColor(String colorSchema) {
        this.colorSchema = colorSchema;
    }

    // Getters
    /**
     * Return a color representing the value in the chosen color schema
     *
     * @param colorValue	the single value to be changed to a color.
     * @return returns jawa.awt.Color.
     */
    public Color getColor(float colorValue) {
        if ("Blue & Red".equals(colorSchema)) {
            return getColorProvider().getPointColor(colorValue);
        }
        if ("Black & White".equals(colorSchema)) {
            return Color.getHSBColor(0, 0, colorValue);
        }
        return Color.getHSBColor(40, 40, colorValue);	// fall back to the default color schema
    }

    /**
     * Return a color representing the value in the chosen color schema
     *
     * @param colorValue	the single value to be changed to a color.
     * @return returns jawa.awt.Color.
     */
    public Color getColor(double colorValue) {
        return getColor((float) colorValue);
    }

    // Helper functions
    private ColorProvider getColorProvider() {
        return new ColorProvider();
    }

}
