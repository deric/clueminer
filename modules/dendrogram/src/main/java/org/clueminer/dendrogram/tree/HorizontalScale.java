/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dendrogram.tree;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;

/**
 * Display scale for dendrogram tree - vertical tree (for columns)
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
        g2.rotate(-Math.PI / 2.0);

        g2.setColor(Color.black);
        //verticle line
        // System.out.println("drawing scale at "+distToScale);
        // drawLine(x1, y1,
        //          x2, y2)
        g2.drawLine(0, distToScale,
                -(tree.getTreeHeight() - 1), distToScale);
        //top tick
        g2.drawLine(0, distToScale,
                    0, distToScale + scaleTickLength);
        //middle tick
        g2.drawLine(-(tree.getTreeHeight() - 1) / 2, distToScale,
                -(tree.getTreeHeight() - 1) / 2, distToScale + scaleTickLength);
        //bottom tick
        g2.drawLine(-(tree.getTreeHeight() - 1), distToScale,
                -(tree.getTreeHeight() - 1), distToScale + scaleTickLength);

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
                      (distToScale + scaleTickLength), (tree.getTreeHeight() - 1) / 2 + 4);
        //bottom Label
        textWidth = hfm.stringWidth(decimalFormat.format(tree.getMinTreeHeight()));
        if (textWidth > maxTextWidth) {
            maxTextWidth = textWidth;
        }
        g2.drawString(decimalFormat.format(tree.getMinTreeHeight()),//this.minHeight),
                      (distToScale + scaleTickLength), (tree.getTreeHeight() - 1));

        g2.rotate(-Math.PI / 2.0);
        maxScaleDimension = distToScale + scaleTickLength + maxTextWidth;
    }

    @Override
    public void recalculate() {
        if (hasData()) {
            height = tree.getTreeHeight();
            width = maxScaleDimension + treeScaleSpace;

            realSize.width = insets.left + width + insets.right;
            realSize.height = insets.top + height + insets.bottom;
            setSize(realSize);
            setPreferredSize(realSize);
            setMinimumSize(realSize);
        }
    }
}
