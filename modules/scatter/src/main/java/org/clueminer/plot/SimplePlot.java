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
import com.xeiam.xchart.Series;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.clueminer.gui.BPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic 2D plot that is fast to render.
 *
 * @author deric
 */
public class SimplePlot extends BPanel {

    private Chart chart;
    private final static Logger LOG = LoggerFactory.getLogger(SimplePlot.class);
    private Dimension minSize;
    private boolean initialized = false;

    /**
     * Constructor
     *
     * @param chart
     */
    public SimplePlot(final Chart chart) {
        this.chart = chart;
        initialize();
        this.initialized = true;
    }

    private void initialize() {
        reqSize = new Dimension(chart.getWidth(), chart.getHeight());
        realSize = reqSize;
        setMinimumSize(reqSize);
        this.fitToSpace = false;
    }

    @Override
    public void setMinimumSize(Dimension size) {
        this.minSize = size;
        resetCache();
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }

    @Override
    public void render(Graphics2D g) {
        chart.paint(g, getWidth(), getHeight());
    }

    @Override
    public void sizeUpdated(Dimension size) {
        resetCache();
    }

    @Override
    public boolean hasData() {
        return chart != null && initialized;
    }

    @Override
    public void recalculate() {
        //TODO: update subcomponents size?
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    /**
     * Update a series by updating the X-Axis, Y-Axis and error bar data
     *
     * @param seriesName
     * @param newXData        - set null to be automatically generated as a list of increasing Integers starting from
     *                        1 and ending at the size of the new Y-Axis data list.
     * @param newYData
     * @param newErrorBarData - set null if there are no error bars
     * @return
     */
    public Series updateSeries(String seriesName, Collection<?> newXData, List<? extends Number> newYData, List<? extends Number> newErrorBarData) {

        Series series = chart.getSeriesMap().get(seriesName);
        if (series == null) {
            throw new IllegalArgumentException("Series name >" + seriesName + "< not found!!!");
        }
        if (newXData == null) {
            // generate X-Data
            List<Integer> generatedXData = new ArrayList<>();
            for (int i = 1; i <= newYData.size(); i++) {
                generatedXData.add(i);
            }
            series.replaceData(generatedXData, newYData, newErrorBarData);
        } else {
            series.replaceData(newXData, newYData, newErrorBarData);
        }

        // Re-display the chart
        resetCache();

        return series;
    }

    public Rectangle2D.Double getPlotArea() {
        return chart.getPlotArea();
    }

    public Rectangle.Double translateSelection(Rectangle rect) {
        return chart.translateSelection(rect);
    }

}
