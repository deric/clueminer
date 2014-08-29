package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.gui.BPanel;

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
    private DendrogramMapping data;
    private boolean antialiasing = true;
    private int colorBarHeight;
    protected BufferedImage buffScale;
    private int textWidth;
    private int spaceBetweenBarAndLabels = 5;

    public Legend(DendroPane p) {
        super();
        this.panel = p;
        setBackground(panel.getBackground());
        setOpaque(true);
        //setDoubleBuffered(false);
        fitToSpace = false;
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
        BufferedImage scaleImg = new BufferedImage(colorBarWidth, colorBarHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = scaleImg.createGraphics();

        double range = max - min;
        double inc = range / (double) colorBarHeight;

        int yStart;
        gr.setColor(Color.black);
        gr.drawRect(0, 0, colorBarWidth - 1, colorBarHeight - 1);
        //maximum color is at the top
        double value = max;
        //draws box with colors
        for (int y = 2; y < colorBarHeight; y++) {
            yStart = colorBarHeight - y;
            gr.setColor(panel.getScheme().getColor(value, data));
            value -= inc;
            gr.fillRect(1, yStart, colorBarWidth - 2, 1);
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

        //create color palette
        if (buffScale == null) {
            buffScale = drawData(colorBarWidth, colorBarHeight, min, max);
        }
        //places color bar to canvas
        g.drawImage(buffScale,
                insets.left, insets.top,
                colorBarWidth, colorBarHeight,
                null);

        FontMetrics hfm = g.getFontMetrics();
        // int descent = hfm.getDescent();
        int fHeight = hfm.getHeight();

        g.setColor(Color.black);

        String strMin = String.valueOf(panel.formatNumber(min));
        textWidth = hfm.stringWidth(strMin); //usually longest string FIXME for smartest string width detection
        g.drawString(strMin, colorBarWidth + spaceBetweenBarAndLabels + insets.left, 0 + fHeight);

        String strMid = String.valueOf(panel.formatNumber(mid));
        //textWidth = hfm.stringWidth(strMid);
        g.drawString(strMid, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight / 2 + fHeight);
        String strMax = String.valueOf(panel.formatNumber(max));
        //textWidth = hfm.stringWidth(strMax);
        g.drawString(strMax, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight + fHeight);
        g.dispose();
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            int cbh = (int) (0.9 * size.height);
            int cbw = (int) Math.min(30, 0.5 * size.width);

            realSize.width = insets.left + cbw + spaceBetweenBarAndLabels + textWidth + insets.right;
            realSize.height = insets.top + cbh + insets.bottom;
            setPreferredSize(realSize);
            setSize(realSize.width, realSize.height - 2);

            if (Math.abs(cbh - colorBarHeight) > 1 || Math.abs(cbw - colorBarWidth) > 1) {
                buffScale = null;
                resetCache();
            }
        }
    }

    @Override
    public boolean hasData() {
        return data != null;
    }

    @Override
    public void recalculate() {
        colorBarHeight = reqSize.height - insets.bottom - insets.top;
        if (colorBarHeight < 10) {
            //default height which is not bellow zero
            colorBarHeight = 20;
        }
        //setMinimumSize(realSize);
    }

    @Override
    public boolean isAntiAliasing() {
        return antialiasing;
    }

    public void setData(DendrogramMapping data) {
        this.data = data;
    }

}
