package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.dendrogram.DendroPane;

/**
 *
 * @author Tomas Barton
 */
public class ColorSchemeImpl implements ColorScheme {

    private final BufferedImage posColorImage = createGradientImage(Color.black, Color.green);
    private final BufferedImage negColorImage = createGradientImage(Color.red, Color.black);
    protected static Color missingColor = new Color(128, 128, 128);
    protected static Color maskColor = new Color(255, 255, 255, 128);
    private final DendroPane panel;

    public ColorSchemeImpl(DendroPane p) {
        panel = p;
    }

    /**
     * Creates a gradient image with specified initial colors.
     *
     * @param color1
     * @param color2
     * @return
     */
    @Override
    public BufferedImage createGradientImage(Color color1, Color color2) {
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
        if (panel.useDoubleGradient()) {
            maximum = value < panel.getDendrogramData().getMidValue() ? panel.getDendrogramData().getMinValue() : panel.getDendrogramData().getMaxValue();
            colorIndex = (int) (255 * (value - panel.getDendrogramData().getMidValue()) / (maximum - panel.getDendrogramData().getMidValue()));
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex = colorIndex > 255 ? 255 : colorIndex;
            rgb = value < panel.getDendrogramData().getMidValue() ? negColorImage.getRGB(255 - colorIndex, 0)
                    : posColorImage.getRGB(colorIndex, 0);
        } else {
            double span = panel.getDendrogramData().getMaxValue() - panel.getDendrogramData().getMinValue();
            if (value <= panel.getDendrogramData().getMinValue()) {
                colorIndex = 0;
            } else if (value >= panel.getDendrogramData().getMaxValue()) {
                colorIndex = 255;
            } else {
                colorIndex = (int) (((value - panel.getDendrogramData().getMinValue()) / span) * 255);
            }
            rgb = posColorImage.getRGB(colorIndex, 0);
        }
        return new Color(rgb);
    }
}
