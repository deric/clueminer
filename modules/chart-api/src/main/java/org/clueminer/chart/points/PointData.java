/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.chart.points;

import java.util.Collections;
import java.util.List;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.data.Row;

/**
 * Class for storing data that will be used to create a data point in a plot.
 */
public class PointData {

    /**
     * Axes that will be used to project the point.
     */
    public final List<Axis> axes;
    /**
     * Renderers for the axes that will be used to project the point.
     */
    public final List<? extends AxisRenderer> axisRenderers;
    /**
     * The data row that will get projected.
     */
    public final Row row;
    /**
     * The index of the column in the row that contains the data value.
     */
    public final int col;

    /**
     * Initializes a new instance with the specified data.
     *
     * @param axes          Axes that are used to project the point.
     * @param axisRenderers Renderers for the axes.
     * @param row           Data row containing that will be projected on the
     *                      axes.
     * @param col           Index of the column in the row that contains the
     *                      data value.
     */
    public PointData(List<Axis> axes, List<? extends AxisRenderer> axisRenderers,
            Row row, int col) {
        this.axes = Collections.unmodifiableList(axes);
        this.axisRenderers = Collections.unmodifiableList(axisRenderers);
        this.row = row;
        this.col = col;
    }
}
