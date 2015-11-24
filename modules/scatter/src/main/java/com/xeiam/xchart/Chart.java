/**
 * Copyright 2011 - 2015 Xeiam LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.xeiam.xchart;

import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.internal.Utils;
import com.xeiam.xchart.internal.chartpart.Axis;
import com.xeiam.xchart.internal.chartpart.ChartPainter;
import com.xeiam.xchart.internal.style.Theme;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An XChart Chart
 *
 * @author timmolter
 */
public class Chart {

    private final ChartPainter chartPainter;

    /**
     * Constructor
     *
     * @param width
     * @param height
     */
    public Chart(int width, int height) {

        chartPainter = new ChartPainter(width, height);
    }

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param chartTheme
     */
    public Chart(int width, int height, ChartTheme chartTheme) {

        this(width, height, chartTheme.newInstance(chartTheme));
    }

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param theme instance of Theme class
     */
    public Chart(int width, int height, Theme theme) {

        chartPainter = new ChartPainter(width, height);
        chartPainter.getStyleManager().setTheme(theme);
    }

    /**
     * Constructor
     *
     * @param chartBuilder
     */
    public Chart(ChartBuilder chartBuilder) {

        this(chartBuilder.width, chartBuilder.height, chartBuilder.chartTheme);
        setChartTitle(chartBuilder.title);
        setXAxisTitle(chartBuilder.xAxisTitle);
        setYAxisTitle(chartBuilder.yAxisTitle);
        getStyleManager().setChartType(chartBuilder.chartType);
    }

    /**
     * @param g
     * @param width
     * @param height
     */
    public void paint(Graphics2D g, int width, int height) {

        chartPainter.paint(g, width, height);
    }

    /**
     * @param g
     */
    public void paint(Graphics2D g) {

        chartPainter.paint(g);
    }

    /**
     * Add a series to the chart using Collections
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param yData the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, Collection<?> xData, Collection<? extends Number> yData) {

        return chartPainter.getAxisPair().addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a Number series to the chart using Collections with error bars
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param yData the Y-Axis data
     * @param errorBars the error bar data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, Collection<?> xData, Collection<? extends Number> yData, Collection<? extends Number> errorBars) {

        return chartPainter.getAxisPair().addSeries(seriesName, xData, yData, errorBars);
    }

    /**
     * Add a series to the chart using double arrays
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param xData the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, double[] xData, double[] yData) {

        return addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a series to the chart using double arrays with error bars
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param xData the Y-Axis data
     * @param errorBars the error bar data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, double[] xData, double[] yData, double[] errorBars) {

        List<Double> xDataNumber = null;
        if (xData != null) {
            xDataNumber = new ArrayList<Double>();
            for (double d : xData) {
                xDataNumber.add(new Double(d));
            }
        }
        List<Double> yDataNumber = new ArrayList<Double>();
        for (double d : yData) {
            yDataNumber.add(new Double(d));
        }
        List<Double> errorBarDataNumber = null;
        if (errorBars != null) {
            errorBarDataNumber = new ArrayList<Double>();
            for (double d : errorBars) {
                errorBarDataNumber.add(new Double(d));
            }
        }

        return chartPainter.getAxisPair().addSeries(seriesName, xDataNumber, yDataNumber, errorBarDataNumber);
    }

    /**
     * Add a series to the chart using int arrays
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param xData the Y-Axis data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, int[] xData, int[] yData) {

        return addSeries(seriesName, xData, yData, null);
    }

    /**
     * Add a series to the chart using int arrays with error bars
     *
     * @param seriesName
     * @param xData the X-Axis data
     * @param xData the Y-Axis data
     * @param errorBars the error bar data
     * @return A Series object that you can set properties on
     */
    public Series addSeries(String seriesName, int[] xData, int[] yData, int[] errorBars) {

        List<Double> xDataNumber = null;
        if (xData != null) {
            xDataNumber = new ArrayList<Double>();
            for (int d : xData) {
                xDataNumber.add(new Double(d));
            }
        }
        List<Double> yDataNumber = new ArrayList<Double>();
        for (int d : yData) {
            yDataNumber.add(new Double(d));
        }
        List<Double> errorBarDataNumber = null;
        if (errorBars != null) {
            errorBarDataNumber = new ArrayList<Double>();
            for (int d : errorBars) {
                errorBarDataNumber.add(new Double(d));
            }
        }

        return chartPainter.getAxisPair().addSeries(seriesName, xDataNumber, yDataNumber, errorBarDataNumber);
    }

    /**
     * Set the chart title
     *
     * @param title
     */
    public void setChartTitle(String title) {

        chartPainter.getChartTitle().setText(title);
    }

    /**
     * Set the x-axis title
     *
     * @param title
     */
    public void setXAxisTitle(String title) {

        chartPainter.getAxisPair().getXAxis().getAxisTitle().setText(title);
    }

    /**
     * Set the y-axis title
     *
     * @param title
     */
    public void setYAxisTitle(String title) {

        chartPainter.getAxisPair().getYAxis().getAxisTitle().setText(title);
    }

    /**
     * Gets the Chart's style manager, which can be used to customize the
     * Chart's appearance
     *
     * @return the style manager
     */
    public StyleManager getStyleManager() {

        return chartPainter.getStyleManager();
    }

    public int getWidth() {

        return chartPainter.getWidth();
    }

    public int getHeight() {

        return chartPainter.getHeight();
    }

    public Map<String, Series> getSeriesMap() {

        return chartPainter.getAxisPair().getSeriesMap();
    }

    /**
     * Convert integer coordinates over chart component to double values
     * corresponding to data in that area.
     *
     * @param rect
     * @return
     */
    public Rectangle.Double translateSelection(Rectangle rect) {
        //rectangle with
        Rectangle2D bounds = chartPainter.getPlot().getBounds();

        Axis xAxis = chartPainter.getAxisPair().getXAxis();
        Axis yAxis = chartPainter.getAxisPair().getYAxis();

        //double xval = rect.x - xLeftMargin / 2.0;
        double xval, xwidth;
        double xmax = bounds.getMaxX();
        double ymax = bounds.getMaxY();
        //double yval = rect.y - yTopMargin / 2.0;
        double yval, yheight;

        xval = rect.x;
        xwidth = rect.getWidth();
        //selection of out plot area
        if (xval < bounds.getX()) {
            xval = bounds.getX();
            xwidth = rect.getMaxX() - bounds.getX();
        }

        yval = rect.y;
        yheight = rect.getHeight();
        if (yval < bounds.getY()) {
            yval = bounds.getY();
            yheight = rect.getMaxY() - bounds.getY();
        }

        Rectangle.Double res = new Rectangle.Double();
        res.x = scaleToRange(xval, bounds.getX(), xmax, xAxis.getMin(), xAxis.getMax());
        //on canvas [0,0] is in top left corner, while on chart it's down left
        res.y = scaleToRange(yval, bounds.getY(), ymax, yAxis.getMax(), yAxis.getMin());
        res.width = (xAxis.getMax() - xAxis.getMin()) * (xwidth / bounds.getWidth());
        res.height = (yAxis.getMax() - yAxis.getMin()) * (yheight / bounds.getHeight());
        return res;
    }

    public double[] translate(Point2D point) {
        double[] pos = new double[2];

        //rectangle with
        Rectangle2D bounds = chartPainter.getPlot().getBounds();

        Axis xAxis = chartPainter.getAxisPair().getXAxis();
        Axis yAxis = chartPainter.getAxisPair().getYAxis();

        //double xval = rect.x - xLeftMargin / 2.0;
        double xval;
        //double yval = rect.y - yTopMargin / 2.0;
        double yval;
        double xmax = bounds.getMaxX();
        double ymax = bounds.getMaxY();

        xval = point.getX();
        //selection of out plot area
        if (xval < bounds.getX()) {
            xval = bounds.getX();
        }

        yval = point.getY();
        if (yval < bounds.getY()) {
            yval = bounds.getY();
        }

        pos[0] = scaleToRange(xval, bounds.getX(), xmax, xAxis.getMin(), xAxis.getMax());
        pos[1] = scaleToRange(yval, bounds.getY(), ymax, yAxis.getMax(), yAxis.getMin());
        return pos;
    }

    /**
     * Scale value from one linear scale to another
     *
     * @param value
     * @param fromRangeMin
     * @param fromRangeMax
     * @param toRangeMin
     * @param toRangeMax
     * @return value scaled to given range
     */
    private double scaleToRange(double value, double fromRangeMin, double fromRangeMax, double toRangeMin, double toRangeMax) {
        return ((value - fromRangeMin) * (toRangeMax - toRangeMin) / (fromRangeMax - fromRangeMin) + toRangeMin);
    }

    /**
     * Given data values [x, y] compute its position on the chart
     *
     * @param x
     * @param y
     * @return
     */
    public Point2D positionOnCanvas(double x, double y) {
        Rectangle2D bounds = chartPainter.getPlot().getBounds();

        StyleManager styleManager = chartPainter.getStyleManager();

        double xMin = chartPainter.getAxisPair().getXAxis().getMin();
        double xMax = chartPainter.getAxisPair().getXAxis().getMax();

        double yMin = chartPainter.getAxisPair().getYAxis().getMin();
        double yMax = chartPainter.getAxisPair().getYAxis().getMax();

        // X-Axis
        double xTickSpace = styleManager.getAxisTickSpacePercentage() * bounds.getWidth();
        double xLeftMargin = Utils.getTickStartOffset((int) bounds.getWidth(), xTickSpace);

        // Y-Axis
        double yTickSpace = styleManager.getAxisTickSpacePercentage() * bounds.getHeight();
        double yTopMargin = Utils.getTickStartOffset((int) bounds.getHeight(), yTickSpace);

        double xTransform = xLeftMargin + ((x - xMin) / (xMax - xMin) * xTickSpace);
        double yTransform = bounds.getHeight() - (yTopMargin + (y - yMin) / (yMax - yMin) * yTickSpace);

        return new Point2D.Double(bounds.getX() + xTransform, bounds.getY() + yTransform);
    }

    public Rectangle2D.Double getPlotArea() {
        Rectangle2D bounds = chartPainter.getPlot().getBounds();

        Rectangle.Double res = new Rectangle.Double();
        res.x = bounds.getX();
        res.y = bounds.getY();
        res.width = bounds.getWidth();
        res.height = bounds.getHeight();
        return res;
    }

}
