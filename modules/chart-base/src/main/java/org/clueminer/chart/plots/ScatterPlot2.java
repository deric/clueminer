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
package org.clueminer.chart.plots;

import java.util.HashMap;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.base.Grid;
import org.clueminer.chart.base.OutsideAxis;
import org.clueminer.chart.renderer.LinearRenderer2D;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class ScatterPlot2 extends ScatterPlot {

    public ScatterPlot2(int width, int height) {
        initComponents(width, height);
    }

    private void initComponents(int width, int height) {
        grid = new Grid(this);
        setBounds(0, 0, width, height);
        axes = new HashMap<>(2);
        axes.put(AxisPosition.X, createAxis(false, Orientation.HORIZONTAL));
        axes.put(AxisPosition.Y, createAxis(false, Orientation.VERTICAL));
        setInsets(insets);
        add(grid);
    }

    protected Axis createAxis(boolean isLogscale, Orientation orient) {
        Axis ax;
        if (isLogscale) {
            throw new UnsupportedOperationException("not supported yet");
        } else {
            ax = new OutsideAxis(new LinearRenderer2D(), orient);
        }
        add(ax);
        return ax;
    }

}
