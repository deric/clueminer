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
package org.clueminer.colors;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.clueminer.gui.ColorPalette;

/**
 *
 * @author Tomas Barton
 */
public class ColorScheme implements ColorPalette {

    private BufferedImage posColorImage;
    private BufferedImage negColorImage;
    protected static Color missingColor = new Color(128, 128, 128);
    protected static Color maskColor = new Color(255, 255, 255, 128);
    private boolean useDoubleGradient = true;
    private double max;
    private double min;
    private double mid;

    public ColorScheme() {
        updateColors(Color.red, Color.black, Color.green);
    }

    public ColorScheme(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

    public ColorScheme(Color min, Color max) {
        this.useDoubleGradient = false;
        updateColors(min, Color.gray, max);
    }

    public ColorScheme(Color min, Color mid, Color max) {
        this.useDoubleGradient = true;
        updateColors(min, mid, max);
    }

    @Override
    public double getMax() {
        return max;
    }

    /**
     * Middle value of interval
     *
     * @return median
     */
    @Override
    public double getMid() {
        return mid;
    }

    @Override
    public double getMin() {
        return min;
    }

    /**
     * Creates a gradient image with specified initial colors.
     */
    private BufferedImage createGradientImage(Color color1, Color color2) {
        BufferedImage image = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(256, 1);
        Graphics2D graphics = image.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, color1, 255, 0, color2);
        graphics.setPaint(gp);
        graphics.drawRect(0, 0, 255, 1);
        return image;
    }

    @Override
    public Color getColor(double value) {
        if (Double.isNaN(value)) {
            return missingColor;
        }

        double maximum;
        int colorIndex, rgb;
        if (useDoubleGradient) {
            maximum = value < getMid() ? getMin() : getMax();
            colorIndex = (int) (255 * (value - getMid()) / (maximum - getMid()));
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex = colorIndex > 255 ? 255 : colorIndex;
            rgb = value < getMid() ? negColorImage.getRGB(255 - colorIndex, 0)
                    : posColorImage.getRGB(colorIndex, 0);
        } else {
            double span = getMax() - getMin();
            if (value <= getMin()) {
                colorIndex = 0;
            } else if (value >= getMax()) {
                colorIndex = 255;
            } else {
                colorIndex = (int) (((value - getMin()) / span) * 255);
            }
            rgb = posColorImage.getRGB(colorIndex, 0);
        }
        return new Color(rgb);
    }

    public final void updateColors(Color min, Color mid, Color max) {
        posColorImage = createGradientImage(mid, max);
        negColorImage = createGradientImage(min, mid);
    }

    @Override
    public void setRange(double min, double max) {
        this.min = min;
        this.max = max;
        this.mid = countMedian(min, max);
    }

    protected double countMedian(double minValue, double maxValue) {
        //in case of negative min, we add it again
        //@test [10-(-5)] /2 + (-5) = 7.5 - 5 = 2.5
        //@test 10 - 0 = 5 + 0 = 5
        //@test 1 - (-1) = 1-1 = 0
        return (maxValue - minValue) / 2 + minValue;
    }

    public boolean isUseDoubleGradient() {
        return useDoubleGradient;
    }

    public void setUseDoubleGradient(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

    /**
     * {@inheritDoc }
     *
     * @param bg
     * @return
     */
    @Override
    public Color complementary(Color bg) {
        double y = (299 * bg.getRed() + 587 * bg.getGreen() + 114 * bg.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;

        //this return purle colors etc. - too colorful
        /*  Color textColor = new Color(255 - bg.getRed(),
         255 - bg.getGreen(),
         255 - bg.getBlue());

         return textColor;*/
    }

}
