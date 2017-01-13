/*
 * Copyright (C) 2011-2017 clueminer.org
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.text.DecimalFormat;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.gui.BPanel;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractScale extends BPanel implements DendrogramDataListener, Serializable {

    private static final long serialVersionUID = 7723360865480072319L;
    //distance between tree and scale
    protected int treeScaleSpace = 10;
    protected int scaleTickLength = 5;
    /**
     * max width/height of ticks + text (number) label
     */
    protected int maxScaleDimension = 30;
    protected int scaleLabelDistance = 10;
    protected int distToScale = 0;
    protected DendrogramTree tree;
    protected Font defaultFont = new Font("verdana", Font.PLAIN, 10);
    protected DendroPane panel;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");
    protected Insets insets = new Insets(0, 0, 0, 0);
    protected int width = 0;
    protected int height = 0;

    protected abstract void drawScale(Graphics2D g2);

    public AbstractScale(DendroPane panel) {
        setDoubleBuffered(false);
        this.panel = panel;
        this.fitToSpace = false;
        this.preserveAlpha = true;
    }

    @Override
    public void render(Graphics2D g) {
        if (!hasData()) {
            return;
        }
        //g.setComposite(AlphaComposite.Src);
        g.setFont(defaultFont);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawScale(g);
        g.dispose();
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        resetCache();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //do nothing, we don't care about height change
    }

    @Override
    public boolean hasData() {
        return (tree != null && tree.hasData());
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            if (size.width > 0 && size.height > 0) {
                reqSize = size;
                resetCache(); //calls recalculate
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
