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
package org.clueminer.clustering.preview;

import java.awt.Color;
import java.awt.GridBagLayout;
import org.math.plot.PlotPanel;
import org.math.plot.canvas.Plot2DCanvas;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlotPanel extends PlotPanel {

    private static final long serialVersionUID = -939469889909029861L;

    public ScatterPlotPanel(){
        super(new Plot2DCanvas());
        initComponents();
    }

    private void initComponents(){
        setLayout(new GridBagLayout());

    }

    @Override
    public int addPlot(String type, String name, Color c, double[]... v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
