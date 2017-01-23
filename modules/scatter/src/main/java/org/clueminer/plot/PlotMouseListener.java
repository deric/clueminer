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
package org.clueminer.plot;

import com.xeiam.xchart.Chart;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class PlotMouseListener<E extends Instance> extends MouseAdapter implements MouseListener, MouseMotionListener {

    private Chart chart;
    private Plotter<E> plotter;
    private static final Logger LOG = LoggerFactory.getLogger(PlotMouseListener.class);

    public PlotMouseListener(Chart chart, Plotter<E> plotter) {
        super();
        this.chart = chart;
        this.plotter = plotter;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        LOG.debug("mouse clicked");
        double[] pos = chart.translate(e.getPoint());
        LOG.debug("got position [{}, {}]", pos[0], pos[1]);
        E[] neighbors = plotter.instanceAt(pos, 5);
        LOG.debug("found {}", neighbors.length);
        for (int i = 0; i < neighbors.length; i++) {
            E neighbor = neighbors[i];
            LOG.debug("inst {}, {}: {}", i, neighbor.getName(), neighbor.asArray());
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        LOG.debug("mouse pressed {}", e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        double[] pos = chart.translate(e.getPoint());
        LOG.debug("mouse moved [{}, {}]", pos[0], pos[1]);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        double[] pos = chart.translate(e.getPoint());
        LOG.debug("mouse exited [{}, {}]", pos[0], pos[1]);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        LOG.debug("mouse dragged");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        LOG.debug("mouse moooved {}", e);
    }

}
