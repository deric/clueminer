package org.clueminer.som.gui.plotter.charts;

import java.awt.Color;

/**
 * Provides colors for Self-Organizing Map. The colors are drawn
 * from a specified colormap. The default colormap schema is blueAndred.
 *
 * @author Jan Motl
 */
public class SOMColor {

    private String colorSchema = "blueAndRed";	// the default color schema to use

    private Color minColor = Color.BLUE;
    private Color maxColor = Color.RED;

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
            return getPointColor(colorValue);
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

    public Color getPointColor(double value) {
        return getPointColor(value, 255);
    }

    public Color getPointColor(double value, int alpha) {
        if (Double.isNaN(value)) {
            return Color.LIGHT_GRAY;
        }
        Color MIN_LEGEND_COLOR = minColor;
        Color MAX_LEGEND_COLOR = maxColor;
        float[] minCol = Color.RGBtoHSB(MIN_LEGEND_COLOR.getRed(), MIN_LEGEND_COLOR.getGreen(), MIN_LEGEND_COLOR.getBlue(), null);
        float[] maxCol = Color.RGBtoHSB(MAX_LEGEND_COLOR.getRed(), MAX_LEGEND_COLOR.getGreen(), MAX_LEGEND_COLOR.getBlue(), null);
        //double hColorDiff = 1.0f - 0.68f;
        double hColorDiff = maxCol[0] - minCol[0];
        double sColorDiff = maxCol[1] - minCol[1];
        double bColorDiff = maxCol[2] - minCol[2];

        Color color = new Color(Color.HSBtoRGB((float) (minCol[0] + hColorDiff * value), (float) (minCol[1] + value * sColorDiff), (float) (minCol[2] + value * bColorDiff)));

        if (alpha < 255) {
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }
        return color;
    }

}
