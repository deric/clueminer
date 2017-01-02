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

import org.clueminer.chart.base.AbstractPlot;
import org.clueminer.chart.theme.ChartTheme;

/**
 *
 * @author deric
 */
public class ChartBuilder {

    int width = 800;
    int height = 600;
    String title = "";
    String xAxisTitle = "";
    String yAxisTitle = "";
    ChartTheme chartTheme;

    public ChartBuilder width(int width) {
        this.width = width;
        return this;
    }

    public ChartBuilder height(int height) {
        this.height = height;
        return this;
    }

    public ChartBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ChartBuilder xAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public ChartBuilder yAxisTitle(String yAxisTitle) {

        this.yAxisTitle = yAxisTitle;
        return this;
    }

    public ChartBuilder theme(ChartTheme chartTheme) {
        this.chartTheme = chartTheme;
        return this;
    }

    /**
     * return fully built Chart
     *
     * @return a Chart
     */
    public AbstractPlot build() {
        return new ScatterPlot(this);

    }

}
