package org.clueminer.dendrogram.tree;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;

/**
 *
 * @author Tomas Barton
 */
public class HorizontalScale extends AbstractScale implements DendrogramDataListener {

    private static final long serialVersionUID = 7372573252024305540L;

    public HorizontalScale(DendrogramTree tree, DendroPane panel) {
        super(panel);
        this.tree = tree;
    }

    @Override
    protected void drawScale(Graphics2D g2) {
        distToScale = scaleLabelDistance;
        setBackground(panel.getBackground());
        g2.rotate(-Math.PI / 2.0);

        g2.setColor(Color.black);
        //verticle line
        // System.out.println("drawing scale at "+distToScale);
        g2.drawLine(0, distToScale,
                    -(tree.getHeight() - 1), distToScale);
        //top tick
        g2.drawLine(0, distToScale,
                    0, distToScale + scaleTickLength);
        //middle tick
        g2.drawLine(-(tree.getHeight() - 1) / 2, distToScale,
                    -(tree.getHeight() - 1) / 2, distToScale + scaleTickLength);
        //bottom tick
        g2.drawLine(-(tree.getHeight() - 1), distToScale,
                    -(tree.getHeight() - 1), distToScale + scaleTickLength);

        g2.rotate(Math.PI / 2.0);
        int textWidth;
        int maxTextWidth = 0;
        FontMetrics hfm = g2.getFontMetrics();
        //top Label
        textWidth = hfm.stringWidth(decimalFormat.format(tree.getMaxTreeHeight()));
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(decimalFormat.format(tree.getMaxTreeHeight()),
                      (distToScale + scaleTickLength), 10);
        //mid Label
        textWidth = hfm.stringWidth(decimalFormat.format(tree.getMidTreeHeight()));
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(decimalFormat.format(tree.getMidTreeHeight()),//(this.maxHeight -this.minHeight)/2+this.minHeight),
                      (distToScale + scaleTickLength), (tree.getHeight() - 1) / 2 + 4);
        //bottom Label
        textWidth = hfm.stringWidth(decimalFormat.format(tree.getMinTreeHeight()));
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(decimalFormat.format(tree.getMinTreeHeight()),//this.minHeight),
                      (distToScale + scaleTickLength), (tree.getHeight() - 1));

        g2.rotate(-Math.PI / 2.0);
        maxScaleDimension = distToScale + scaleTickLength + maxTextWidth;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        updateSize();
        bufferedImage = null;
        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //do nothing, we don't care about height change
    }

    @Override
    public void updateSize() {
        int width = maxScaleDimension + treeScaleSpace;
        int height = tree.getHeight();
        setDimension(width, height);
    }
}
