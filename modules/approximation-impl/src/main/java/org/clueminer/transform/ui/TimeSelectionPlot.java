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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
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

    public TimeSelectionPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
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
        for (ContinuousInstance inst : dataset) {
            plot.addInstance(inst);
        }
    }

    @Override
    public void render(Graphics2D g) {

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

    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //
        if (start != null) {
            Point end = e.getPoint();
            rectangle = new Rectangle(start, new Dimension(end.x - start.x, end.y - start.y));
            LOG.info("got area {}", rectangle);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        start = e.getPoint();
        LOG.info("moooved {}", e);
    }

}
