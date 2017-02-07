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
package org.clueminer.transform.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.locks.ReentrantLock;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.gui.BPanel;
import org.clueminer.plot.TimeXPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class TimeSelectionPlot extends BPanel implements MouseMotionListener {

    private TimeXPlot plot;
    private Timeseries<? extends ContinuousInstance> dataset;
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    private static final Logger LOG = LoggerFactory.getLogger(TimeSelectionPlot.class);
    private Point start;
    private Rectangle rectangle;
    private final ReentrantLock lock = new ReentrantLock();
    private final CropTimeseriesUI flowUI;
    private double prevStart = Double.NaN;
    private javax.swing.Box.Filler filler1;

    public TimeSelectionPlot(CropTimeseriesUI parent) {
        this.flowUI = parent;
        //we can't initialize plot without data
        setLayout(new GridBagLayout());
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 359;
        c.ipady = 253;
        c.anchor = java.awt.GridBagConstraints.NORTHWEST;
        c.insets = new java.awt.Insets(6, 6, 0, 0);
        add(filler1, c);
        setMinimumSize(new Dimension(800, 600));
    }

    private void initComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;

        plot = new TimeXPlot(800, 600, false);
        add(plot, c);
        plot.getChartPanel().addMouseMotionListener(this);
    }

    public void setDataset(Timeseries<? extends ContinuousInstance> dataset) {
        this.dataset = dataset;
        this.removeAll();
        initComponents();
        for (ContinuousInstance inst : dataset) {
            plot.addInstance(inst);
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (rectangle != null) {
            drawRectangle(g, rectangle);
        }
    }

    /**
     * Draws selection rectangle
     *
     * @param g
     * @param rect
     */
    private void drawRectangle(Graphics2D g, Rectangle rect) {
        g.setStroke(dashed);
        g.setColor(Color.GRAY);
        g.draw(rect);

        /**
         * +----------------+
         * | +-----------+ |
         * | | excluded_ | |
         * | | __area___ | |
         * | +-----------+ |
         * +---------------+
         */
        //instead of filling the rectangle we fill the surrounding (thus dimm it a bit)
        Rectangle2D.Double plotArea = plot.getChartPanel().getPlotArea();
        g.setColor(new Color(255, 255, 255, 150));
        int x = (int) plotArea.x;
        int y = (int) plotArea.y;
        int w = (int) plotArea.width;
        int h = (int) plotArea.height;

        //left side
        g.fillRect(x, y, rect.x - x, h);
        //right size
        g.fillRect(rect.x + rect.width, y, w - rect.x - x, h);

        Rectangle2D.Double transRect = plot.getChartPanel().translateSelection(rect);
        if (prevStart != transRect.x) {
            flowUI.setStart(transRect.x);
        }
        prevStart = transRect.x;
        flowUI.setEnd(transRect.x + transRect.width);
    }

    @Override
    public void paint(Graphics g) {
        lock.lock();
        try {
            super.paint(g);
            render((Graphics2D) g);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void sizeUpdated(Dimension size) {
        setSize(size);
        resetCache();
    }

    @Override
    public boolean hasData() {
        return dataset != null;
    }

    @Override
    public void recalculate() {
        if (plot != null) {
            realSize = plot.getSize();
        }
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        resetCache();
        if (start != null) {
            Point end = e.getPoint();
            Rectangle2D.Double plotArea = plot.getChartPanel().getPlotArea();
            rectangle = new Rectangle(start.x, (int) plotArea.y + 1, end.x - start.x, (int) plotArea.height + 1);

            if (bufferedImage != null) {
                Graphics2D g = bufferedImage.createGraphics();
                g.drawImage(bufferedImage, 0, 0, null);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        start = e.getPoint();
    }

}
