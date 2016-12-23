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
package org.clueminer.dendrogram.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 */
public class CutoffLine extends BPanel implements DendrogramDataListener {

    private static final long serialVersionUID = -8874221664051165124L;
    private final DendroPane panel;
    private final DendrogramTree tree;
    private HierarchicalResult hclust;
    final static float dash1[] = {10.0f};
    //start at tree root
    private int linepos = 100;
    final static BasicStroke dashed
            = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);
    private static final RequestProcessor RP = new RequestProcessor("computing new cutoff");
    private int sliderDiameter = 6;
    private final StdScale scale;
    private final boolean antialiasing = true;

    public CutoffLine(DendroPane p, DendrogramTree tree) {
        this.panel = p;
        this.tree = tree;
        this.sliderDiameter = p.getSliderDiameter();
        this.setOpaque(false); //don't paint background (parent component is responsible for that)
        this.scale = new StdScale();
        this.preserveAlpha = true;
    }

    /**
     * Draws dashed line, position is translated into interval where minimum is
     * slider diameter (however it's mirrored, so it becomes maximum). The other
     * side of interval is defined by the width of the component.
     *
     * @param g2
     */
    private void drawLine(Graphics2D g2) {
        g2.setComposite(AlphaComposite.Src);
        g2.setColor(Color.RED);
        //there's a gap on tree root side which is equal to sliderDiameter
        //nice trick how to "inverse" scale
        linepos = computePosition(hclust, getWidth() - sliderDiameter, sliderDiameter);
        g2.setStroke(dashed);
        //draw dashed line across whole tree width
        // x1, y1, x2, y2
        g2.drawLine(linepos, 0, linepos, getHeight());
    }

    /**
     * Computes position of line on the dendrogram tree
     *
     * @param hres
     * @param min  target interval minimum
     * @param max  target interval maximum
     *
     * @return
     */
    protected int computePosition(HierarchicalResult hres, int min, int max) {
        /**
         * TODO cutoff is probably being updated somewhere else
         */
        double cut = hres.getCutoff();
        return (int) scale.scaleToRange(cut, 0, hres.getMaxTreeHeight(), min, max);
    }

    /**
     * Translates cutoff
     *
     * @param pos position from 0 to 100
     * @return
     */
    private double computeCutoff(int pos) {
        //min tree distance is distance of lowest level, not leaves!
        if (hasData()) {
            double cut = (pos * hclust.getMaxTreeHeight() / 100.0);
            //inverse value (slider min is on left)
            return cut;
        } else {
            return 0.0;
        }
    }

    public int getLinePosition() {
        return linepos;
    }

    public double getMaxDistance() {
        return tree.getTreeHeight();
    }

    public double getMinDistance() {
        return tree.getTreeWidth();
    }

    /**
     * Cutoff is in range from 0 to max_pixels of dendrogram tree
     *
     * @param pos
     * @param isAdjusting
     */
    public void setCutoff(int pos, boolean isAdjusting) {
        final double cut = computeCutoff(pos);
        if (!isAdjusting && hclust != null) {
            RequestProcessor.Task task = RP.create(new Runnable() {

                @Override
                public void run() {
                    //quite expensive to compute
                    Clustering c = hclust.updateCutoff(cut);
                    if (c != null) {
                        //@TODO depending on horizontal or vertical position we shoud choose rows or columns
                        panel.fireClusteringChanged(c);
                    }
                    resetCache();
                }
            });
            RP.post(task);
        }
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.hclust = dataset.getRowsResult();
        //updateSize();
        resetCache();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care, but other components are repainting and this layer would otherwise disappear
        resetCache();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //   updateSize();
        resetCache();
    }

    @Override
    public void render(Graphics2D g) {
        if (hasData()) {
            drawLine(g);
        }
        g.dispose();
    }

    @Override
    public void sizeUpdated(Dimension size) {
        this.realSize = size;
    }

    @Override
    public boolean hasData() {
        return hclust != null;
    }

    @Override
    public void recalculate() {
        //not much to do, realSize is already updated
    }

    @Override
    public boolean isAntiAliasing() {
        return antialiasing;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
