package org.clueminer.dendrogram.tree;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.gui.DendrogramPanel;

/**
 * Rows tree scale
 *
 * @author Tomas Barton
 */
public class VerticalScale extends AbstractScale implements DendrogramDataListener {

    private static final long serialVersionUID = 7372573252024305540L;

    public VerticalScale(VerticalTree tree, DendrogramPanel panel) {
        super(panel);
        this.tree = tree;
    }

    @Override
    protected void drawScale(Graphics2D g2) {
        distToScale = 5;
        setBackground(panel.getBackground());
        g2.setColor(Color.black);
        // total tree size minus offset, which is (negative) transition from top
        int yStart = treeScaleSpace;
        // x1, y1, x2, y2
        g2.drawLine(0, yStart - 5, tree.max_pixels - 1, yStart - 5); //bottom line
        g2.drawLine(tree.max_pixels - 1, yStart - 5, tree.max_pixels - 1, yStart + scaleTickLength); //max tick
        g2.drawLine(tree.max_pixels / 2 + 5, yStart - 5, tree.max_pixels / 2 + 5, yStart + scaleTickLength);//mid tick
        g2.drawLine(0, yStart - 5, 0, yStart + scaleTickLength);//min tick

        int space = scaleLabelDistance + scaleTickLength;
        int textWidth;
        int maxTextWidth = 0;
        FontMetrics hfm = g2.getFontMetrics();
        g2.rotate(3 * Math.PI / 2.0);
        //min Label
        textWidth = hfm.stringWidth(tree.getMinHeightDisplay());
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(tree.getMinHeightDisplay(), -(yStart + space + textWidth), tree.max_pixels - 1);

        //mid Label
        textWidth = hfm.stringWidth(tree.getMidHeightDisplay());
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(tree.getMidHeightDisplay(), -(yStart + space + textWidth), tree.max_pixels / 2 + 9);

        //max Label
        textWidth = hfm.stringWidth(tree.getMaxHeightDisplay());
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(tree.getMaxHeightDisplay(), -(yStart + space + textWidth), 10);
        g2.setColor(Color.black);
        g2.rotate(Math.PI / 2.0);
        maxScaleDimension = distToScale + space + maxTextWidth;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        updateSize();
        bufferedImage = null;
        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //do nothing, we don't care about width change
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //not important right now, as long as the tree scale doesn't change
    }

    @Override
    protected void updateSize() {
        int width = tree.max_pixels;
        int height = maxScaleDimension + treeScaleSpace;
        setDimension(width, height);
    }
}
