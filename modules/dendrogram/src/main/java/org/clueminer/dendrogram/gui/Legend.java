package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.Distribution;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;

/**
 * Displays color scale for range of numbers
 *
 * @TODO implement horizontal version
 * @author Tomas Barton
 */
public class Legend extends BPanel implements DendrogramDataListener {

    private static final long serialVersionUID = 5461063176271490884L;
    private final Insets insets = new Insets(10, 10, 10, 0);
    private final DendroPane panel;
    private int colorBarWidth = 30;
    private int colorBarHeight;
    private DendrogramMapping data;
    private boolean antialiasing = true;
    protected BufferedImage buffScale;
    private int spaceBetweenBarAndLabels = 5;
    private int orientation;
    private int fHeight;
    private int maxStrWidth = 10;
    private Dimension available;

    public Legend(DendroPane p) {
        super();
        this.panel = p;
        setBackground(panel.getBackground());
        setOpaque(true);
        //setDoubleBuffered(false);
        fitToSpace = false;
        this.orientation = SwingConstants.VERTICAL;
        available = new Dimension(0, 0);
    }

    /**
     * Either SwingConstants.VERTICAL or HORIZONTAL
     *
     * @param orientation component orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
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
    private BufferedImage drawData(int colorBarWidth, int colorBarHeight, double min, double max) {
        BufferedImage scaleImg;
        Graphics2D gr;

        double range = max - min;
        double inc = range / (double) colorBarHeight;

        scaleImg = new BufferedImage(colorBarWidth, colorBarHeight, BufferedImage.TYPE_INT_ARGB);
        gr = scaleImg.createGraphics();

        gr.setColor(Color.black);
        gr.drawRect(0, 0, colorBarWidth - 1, colorBarHeight - 1);

        Distribution dist = panel.getDistribution();
        int[] histRange = null;
        StdScale scale = null;
        double histVal = 0.0;
        if (dist != null) {
            histRange = dist.binsRange();
            scale = new StdScale();
            //make sure we fit into the bin
            histVal = max - dist.getStep() / 2.0;
        }

        if (orientation == SwingConstants.VERTICAL) {
            int yStart;
            //maximum color is at the top
            double value = max;
            int scaleMin = colorBarWidth / 10;
            int histWidth;
            //draws box with colors
            for (int y = 2; y < colorBarHeight; y++) {
                yStart = colorBarHeight - y;
                gr.setColor(panel.getScheme().getColor(value, data));
                value -= inc;
                histVal -= inc;
                if (dist != null && scale != null) {
                    if (histVal < min) {
                        histVal = min;
                    }
                    histWidth = (int) scale.scaleToRange(dist.hist(histVal), histRange[0], histRange[1], scaleMin, colorBarWidth - 2);
                } else {
                    histWidth = colorBarWidth - 2;
                }
                gr.fillRect(1, yStart, histWidth, 1);
            }
        } else {
            int xStart;
            int scaleMin = colorBarHeight / 10;
            int histWidth;
            inc = range / (double) colorBarWidth;
            //maximum color is at the left side
            double value = max;
            //draws box with colors
            for (int x = 2; x < colorBarWidth; x++) {
                xStart = colorBarWidth - x;
                gr.setColor(panel.getScheme().getColor(value, data));
                value -= inc;
                histVal -= inc;
                if (dist != null && scale != null) {
                    if (histVal < min) {
                        histVal = min;
                    }
                    histWidth = (int) scale.scaleToRange(dist.hist(histVal), histRange[0], histRange[1], scaleMin, colorBarHeight - 2);
                } else {
                    histWidth = colorBarHeight - 2;

                }
                gr.fillRect(xStart, 1, 1, histWidth);
            }
        }
        gr.dispose();
        return scaleImg;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping mapping) {
        data = mapping;
        buffScale = null;
        resetCache();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //do nothing
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //do nothing
    }

    @Override
    public void render(Graphics2D g) {
        double min = data.getMinValue();
        double max = data.getMaxValue();
        double mid = data.getMidValue();

        FontMetrics hfm = g.getFontMetrics();
        // int descent = hfm.getDescent();
        fHeight = hfm.getHeight();

        g.setColor(Color.black);

        //create color palette
        if (buffScale == null) {
            buffScale = drawData(colorBarWidth, colorBarHeight, min, max);
        }
        //places color bar to canvas
        g.drawImage(buffScale,
                insets.left, insets.top,
                colorBarWidth, colorBarHeight,
                null);

        if (orientation == SwingConstants.VERTICAL) {

            String strMin = String.valueOf(panel.formatNumber(min));
            checkMaxString(hfm.stringWidth(strMin));
            g.drawString(strMin, colorBarWidth + spaceBetweenBarAndLabels + insets.left, 0 + fHeight);
            String strMid = String.valueOf(panel.formatNumber(mid));
            checkMaxString(hfm.stringWidth(strMid));
            g.drawString(strMid, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight / 2 + fHeight);
            String strMax = String.valueOf(panel.formatNumber(max));
            checkMaxString(hfm.stringWidth(strMax));
            g.drawString(strMax, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight + fHeight);
        } else {
            //horizontal
            String strMin = String.valueOf(panel.formatNumber(min));

            int strWidth = hfm.stringWidth(strMin);
            checkMaxString(strWidth);
            g.drawString(strMin, strWidth / 2, insets.top + colorBarHeight + fHeight);
            String strMid = String.valueOf(panel.formatNumber(mid));
            strWidth = hfm.stringWidth(strMid);
            checkMaxString(strWidth);
            g.drawString(strMid, colorBarWidth / 2 + strWidth / 2, insets.top + colorBarHeight + fHeight);
            String strMax = String.valueOf(panel.formatNumber(max));
            strWidth = hfm.stringWidth(strMax);
            checkMaxString(strWidth);
            g.drawString(strMax, colorBarWidth - strWidth / 2, insets.top + colorBarHeight + fHeight);

        }
        g.dispose();
    }

    public void setAvailableSpace(int width, int height) {
        available.width = width;
        available.height = height;

        recalculate();
        setSize(realSize);
    }

    private void checkMaxString(int width) {
        if (width > maxStrWidth) {
            maxStrWidth = width;
        }
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            recalculate();
            setPreferredSize(realSize);
            setSize(realSize.width, realSize.height - 2);

            if (Math.abs(size.height - realSize.height) > 1 || Math.abs(size.width - realSize.width) > 1) {
                buffScale = null;
                resetCache();
            }
        }
    }

    @Override
    public boolean hasData() {
        return data != null;
    }

    /**
     * Compute space required for legend
     */
    @Override
    public void recalculate() {
        int stdBarSize = 60;

        if (hasData()) {
            if (orientation == SwingConstants.VERTICAL) {
                realSize.height = available.height;
                realSize.width = stdBarSize;
                colorBarHeight = available.height - insets.bottom - insets.top;
            } else {
                realSize.height = stdBarSize + spaceBetweenBarAndLabels;
                if (available.width < 200) {
                    realSize.width = available.width;
                } else {
                    realSize.width = 200;
                }

                colorBarWidth = realSize.width - insets.left - insets.right;
                colorBarHeight = stdBarSize - insets.bottom - insets.top - fHeight - spaceBetweenBarAndLabels;
            }
        }
    }

    @Override
    public boolean isAntiAliasing() {
        return antialiasing;
    }

    public void setData(DendrogramMapping data) {
        this.data = data;
        recalculate();
        setSize(realSize);
        resetCache();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
