package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 *
 * @author Tomas Barton
 */
public class ColorSchemeImpl implements ColorScheme {

    private final BufferedImage posColorImage = createGradientImage(Color.black, Color.green);
    private final BufferedImage negColorImage = createGradientImage(Color.red, Color.black);
    protected static Color missingColor = new Color(128, 128, 128);
    protected static Color maskColor = new Color(255, 255, 255, 128);
    private boolean useDoubleGradient = true;

    public ColorSchemeImpl(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

    public ColorSchemeImpl() {

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

    /**
     * {@inheritDoc}
     *
     * @param value
     * @param dendroData
     * @return
     */
    @Override
    public Color getColor(double value, DendrogramMapping dendroData) {
        if (Double.isNaN(value)) {
            return missingColor;
        }

        double maximum;
        int colorIndex, rgb;
        if (useDoubleGradient) {
            maximum = value < dendroData.getMidValue() ? dendroData.getMinValue() : dendroData.getMaxValue();
            colorIndex = (int) (255 * (value - dendroData.getMidValue()) / (maximum - dendroData.getMidValue()));
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex = colorIndex > 255 ? 255 : colorIndex;
            rgb = value < dendroData.getMidValue() ? negColorImage.getRGB(255 - colorIndex, 0)
                    : posColorImage.getRGB(colorIndex, 0);
        } else {
            double span = dendroData.getMaxValue() - dendroData.getMinValue();
            if (value <= dendroData.getMinValue()) {
                colorIndex = 0;
            } else if (value >= dendroData.getMaxValue()) {
                colorIndex = 255;
            } else {
                colorIndex = (int) (((value - dendroData.getMinValue()) / span) * 255);
            }
            rgb = posColorImage.getRGB(colorIndex, 0);
        }
        return new Color(rgb);
    }

    @Override
    public boolean isUseDoubleGradient() {
        return useDoubleGradient;
    }

    @Override
    public void setUseDoubleGradient(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

}
