package org.clueminer.clustering.gui.colors;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.gui.GraphicsEnv;

/**
 *
 * @author Tomas Barton
 */
public final class ColorSchemeImpl implements ColorScheme {

    private BufferedImage negColorImage = createGradientImage(Color.red, Color.black);
    private BufferedImage posColorImage = createGradientImage(Color.black, Color.green);
    public static Color missingColor = new Color(128, 128, 128);
    public static Color maskColor = new Color(255, 255, 255, 128);
    private boolean useDoubleGradient = true;

    public ColorSchemeImpl(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

    public ColorSchemeImpl() {

    }

    public ColorSchemeImpl(Color c1, Color c2, Color c3) {
        negColorImage = createGradientImage(c1, c2);
        posColorImage = createGradientImage(c2, c3);
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
        BufferedImage image = GraphicsEnv.compatibleImage(256, 1);
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
        return getColor(value, dendroData.getMinValue(), dendroData.getMidValue(), dendroData.getMaxValue());
    }

    @Override
    public boolean isUseDoubleGradient() {
        return useDoubleGradient;
    }

    @Override
    public void setUseDoubleGradient(boolean useDoubleGradient) {
        this.useDoubleGradient = useDoubleGradient;
    }

    @Override
    public Color getColor(double value, double min, double mid, double max) {
        return getColor(value, min, mid, max, 255);
    }

    @Override
    public Color getColor(double value, double min, double mid, double max, int alpha) {
        if (Double.isNaN(value)) {
            return missingColor;
        }

        double maximum;
        int colorIndex, rgb;
        if (useDoubleGradient) {
            maximum = value < mid ? min : max;
            colorIndex = (int) (255 * (value - mid) / (maximum - mid));
            if (colorIndex < 0) {
                colorIndex = -colorIndex;
            }
            colorIndex = colorIndex > 255 ? 255 : colorIndex;
            rgb = value < mid ? negColorImage.getRGB(255 - colorIndex, 0)
                  : posColorImage.getRGB(colorIndex, 0);
        } else {
            double span = max - min;
            if (value <= min) {
                colorIndex = 0;
            } else if (value >= max) {
                colorIndex = 255;
            } else {
                colorIndex = (int) (((value - min) / span) * 255);
            }
            rgb = posColorImage.getRGB(colorIndex, 0);
        }
        if (alpha < 255) {
            return new Color(rgb, true);
        } else {
            //no alpha
            return new Color(rgb);
        }
    }

}
