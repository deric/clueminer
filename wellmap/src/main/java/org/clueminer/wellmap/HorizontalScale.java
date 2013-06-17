package org.clueminer.wellmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.clueminer.gui.ColorPalette;

/**
 *
 * @author Tomas Barton
 */
public class HorizontalScale extends ColorScale {

    private static final long serialVersionUID = -442964336904092850L;

    public HorizontalScale(ColorPalette palette) {
        super(palette);
        colorBarHeight = 30;
        insets = new Insets(10, 10, 10, 10);
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
            bufferedGraphics.fillRect(xStart, 1, 1, colorBarHeight - 2);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        double min = palette.getMin();
        double max = palette.getMax();
        double mid = palette.getMid();

        FontMetrics hfm = g.getFontMetrics();
        String strMin = String.valueOf(decimalFormat.format(min));
        int minTextWidth = hfm.stringWidth(strMin);
        int leftTextOverFlow = minTextWidth / 2;

        String strMid = String.valueOf(decimalFormat.format(mid));
        int midTextWidth = hfm.stringWidth(strMid);

        String strMax = String.valueOf(decimalFormat.format(max));
        int maxTextWidth = hfm.stringWidth(strMax);
        int rightTextOverFlow = maxTextWidth / 2;


        colorBarWidth = this.getWidth() - insets.left - insets.right - leftTextOverFlow - rightTextOverFlow;
        if (colorBarWidth < 10) {
            //default height which is not bellow zero
            colorBarWidth = 20;
        }
        //colorBarHeight -= insets.top + insets.bottom;
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
        //draw ticks
        g2d.setColor(Color.black);
        int yStart = colorBarHeight + insets.top;
        int xPos = insets.left;
        //min tick
        g2d.drawLine(xPos, yStart, xPos, yStart + tickSize);

        //mid tick
        xPos = insets.left + colorBarWidth / 2;
        g2d.drawLine(xPos, yStart, xPos, yStart + tickSize);

        //max tick
        xPos = insets.left + colorBarWidth - 1;
        g2d.drawLine(xPos, yStart, xPos, yStart + tickSize);

        g2d.setColor(Color.black);
        int spaceBetweenBarAndLabels = 5;

        xPos = insets.left - leftTextOverFlow;
        yStart = colorBarHeight + spaceBetweenBarAndLabels + insets.top + insets.bottom + tickSize;
        g2d.drawString(strMin, xPos, yStart);


        xPos = colorBarWidth / 2 + insets.left - midTextWidth / 2;
        g2d.drawString(strMid, xPos, yStart);

        xPos = colorBarWidth - rightTextOverFlow + insets.left;
        g2d.drawString(strMax, xPos, yStart);

        int totalWidth = insets.left + leftTextOverFlow + colorBarWidth + rightTextOverFlow + insets.right;
        int totalHeight = insets.top + colorBarHeight + spaceBetweenBarAndLabels + insets.bottom;
        setMinimumSize(new Dimension(totalWidth, totalHeight));
    }
}
