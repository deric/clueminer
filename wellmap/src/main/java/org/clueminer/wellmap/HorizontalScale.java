package org.clueminer.wellmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.clueminer.gui.ColorPalette;

/**
 *
 * @author Tomas Barton
 */
public class HorizontalScale extends ColorScale {

    public HorizontalScale(ColorPalette palette) {
        super(palette);        
        colorBarHeight = 30;
    }

    /**
     * Filling rectangles is rather expensive operation therefore we try to call
     * it just once
     *
     * @TODO call this methods when color scheme changes
     *
     * @param colorBarWidth
     * @param colorBarHeight
     * @param min
     * @param max
     */
    @Override
    protected void drawData(int colorBarWidth, int colorBarHeight, double min, double max) {
        bufferedImage = new BufferedImage(colorBarWidth, colorBarHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();

        double range = max - min;
        double inc = range / (double) colorBarWidth;

        int xStart;
        bufferedGraphics.setColor(Color.black);
        bufferedGraphics.drawRect(0, 0, colorBarWidth - 1, colorBarHeight - 1);
        //maximum color is at the top
        double value = max;
        //draws box with colors
        for (int x = 2; x < colorBarWidth; x++) {
            xStart = colorBarWidth - x;
            bufferedGraphics.setColor(palette.getColor(value));
            value -= inc;
            bufferedGraphics.fillRect(xStart, 1, colorBarWidth - 2, 1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        double min = palette.getMin();
        double max = palette.getMax();
        double mid = palette.getMid();
        colorBarWidth = this.getWidth() - insets.left - insets.right;
        if (colorBarWidth < 10) {
            //default height which is not bellow zero
            colorBarWidth = 20;
        }
        //create color palette
        if (bufferedImage == null) {
            drawData(colorBarWidth, colorBarHeight, min, max);
        }
        //places color bar to canvas
        g2d.drawImage(bufferedImage,
                insets.left, insets.top,
                colorBarWidth, colorBarHeight,
                null);


        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        FontMetrics hfm = g.getFontMetrics();
        // int descent = hfm.getDescent();
        int fHeight = hfm.getHeight();

        g2d.setColor(Color.black);
        int textWidth;
        int spaceBetweenBarAndLabels = 5;
        String strMin = String.valueOf(decimalFormat.format(min));
        textWidth = hfm.stringWidth(strMin); //usually longest string FIXME for smartest string width detection
        g2d.drawString(strMin, colorBarWidth + spaceBetweenBarAndLabels + insets.left, 0 + fHeight);

        String strMid = String.valueOf(decimalFormat.format(mid));
        //textWidth = hfm.stringWidth(strMid);
        g2d.drawString(strMid, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight / 2 + fHeight);
        String strMax = String.valueOf(decimalFormat.format(max));
        //textWidth = hfm.stringWidth(strMax);
        g2d.drawString(strMax, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight + fHeight);

        int totalWidth = insets.left + colorBarWidth + spaceBetweenBarAndLabels + textWidth + insets.right;
        int totalHeight = insets.top + colorBarHeight + insets.bottom;
        setMinimumSize(new Dimension(totalWidth, totalHeight));
    }
}
