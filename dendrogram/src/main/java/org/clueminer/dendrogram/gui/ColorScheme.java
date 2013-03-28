package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public class ColorScheme {

    private BufferedImage posColorImage = createGradientImage(Color.black, Color.green);
    private BufferedImage negColorImage = createGradientImage(Color.red, Color.black);
    protected static Color missingColor = new Color(128, 128, 128);
    protected static Color maskColor = new Color(255, 255, 255, 128);
    private DendrogramPanel panel;
    
    public ColorScheme(DendrogramPanel p){
        panel = p;
    }

    /**
     * Creates a gradient image with specified initial colors.
     */
    public BufferedImage createGradientImage(Color color1, Color color2) {
        BufferedImage image = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(256, 1);
        Graphics2D graphics = image.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, color1, 255, 0, color2);
        graphics.setPaint(gp);
        graphics.drawRect(0, 0, 255, 1);
        return image;
    }

   public Color getColor(double value) {
        if (Double.isNaN(value)) {
            return missingColor;
        }

        double maximum;
        int colorIndex, rgb;
        if (panel.useDoubleGradient) {
            maximum = value < panel.dendroData.getMidValue() ? panel.dendroData.getMinValue() : panel.dendroData.getMaxValue();
            colorIndex = (int) (255 * (value - panel.dendroData.getMidValue()) / (maximum - panel.dendroData.getMidValue()));
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex = colorIndex > 255 ? 255 : colorIndex;
            rgb = value < panel.dendroData.getMidValue() ? negColorImage.getRGB(255 - colorIndex, 0)
                    : posColorImage.getRGB(colorIndex, 0);
        } else {
            double span = panel.dendroData.getMaxValue() - panel.dendroData.getMinValue();
            if (value <= panel.dendroData.getMinValue()) {
                colorIndex = 0;
            } else if (value >= panel.dendroData.getMaxValue()) {
                colorIndex = 255;
            } else {
                colorIndex = (int) (((value - panel.dendroData.getMinValue()) / span) * 255);
            }
            rgb = posColorImage.getRGB(colorIndex, 0);
        }
        return new Color(rgb);
    }
}
