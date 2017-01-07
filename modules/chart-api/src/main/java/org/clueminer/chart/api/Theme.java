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
package org.clueminer.chart.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.clueminer.chart.util.Location;

/**
 *
 * @author deric
 */
public interface Theme {

    /**
     * Theme unique identification
     *
     * @return
     */
    String getName();

    int getMarkerSize();

    Color getErrorBarsColor();

    boolean isErrorBarsColorSeriesColor();

    /**
     * Returns the paint which is used to fill the background of the plot.
     *
     * @return Paint which is used to fill the background of the plot.
     */
    Color getBackground();

    /**
     * Sets the paint which will be used to fill the background of the plot.
     *
     * @param background Paint which will be used to fill the background of the
     *                   plot.
     */
    void setBackground(Color background);

    /**
     * Returns the stroke which is used to paint the border of the plot.
     *
     * @return Stroke which is used to paint the border of the plot.
     */
    Stroke getBorderStroke();

    /**
     * Sets the stroke which will be used to paint the border of the plot.
     *
     * @param border Stroke which will be used to paint the border of the plot.
     */
    void setBorderStroke(Stroke border);

    /**
     * Returns the paint which is used to fill the border of the plot.
     *
     * @return Paint which is used to fill the border of the plot.
     */
    Color getBorderColor();

    /**
     * Sets the paint which will be used to fill the border of the plot.
     *
     * @param color Paint which will be used to fill the border of the plot.
     */
    void setBorderColor(Color color);

    /**
     * Returns the base font used by the plot.
     *
     * @return Font used by the plot.
     */
    Font getFont();

    /**
     * Sets the base font that will be used by the plot.
     *
     * @param font Font that will used by the plot.
     */
    void setFont(Font font);

    void setChartPadding(int chartPadding);

    int getChartPadding();

    void setChartFontColor(Color color);

    Color getChartFontColor();

    void setAxisTitleFont(Font axisTitleFont);

    Font getAxisTitleFont();

    /**
     * Returns whether the legend is shown.
     *
     * @return {@code true} if the legend is shown,
     *         {@code false} if the legend is hidden.
     */
    boolean isLegendVisible();

    /**
     * Sets whether the legend will be shown.
     *
     * @param legendVisible {@code true} if the legend should be shown,
     *                      {@code false} if the legend should be hidden.
     */
    void setLegendVisible(boolean legendVisible);

    /**
     * Returns the current positioning of the legend inside the plot.
     *
     * @return Current positioning of the legend inside the plot.
     */
    Location getLegendLocation();

    /**
     * Sets the positioning of the legend inside the plot.
     *
     * @param location Positioning of the legend inside the plot.
     */
    void setLegendLocation(Location location);

    /**
     * Returns the spacing between the plot area and the legend.
     *
     * @return Spacing between the plot area and the legend relative to font
     *         height.
     */
    double getLegendDistance();

    /**
     * Sets the spacing between the plot area and the legend.
     * The distance is defined in font height.
     *
     * @param distance Spacing between the plot area and the legend relative to
     *                 font
     *                 height.
     */
    void setLegendDistance(double distance);

    boolean isXAxisTitleVisible();

    void setXAxisTitleVisible(boolean xAxisTitleVisible);

    boolean isYAxisTitleVisible();

    void setYAxisTitleVisible(boolean yAxisTitleVisible);

    /**
     * Set the chart title visibility
     *
     * @param isChartTitleVisible
     */
    void setChartTitleVisible(boolean isChartTitleVisible);

    boolean isChartTitleVisible();

    /**
     * Sets the padding between the axis title and the tick labels
     *
     * @param axisTitlePadding
     */
    void setAxisTitlePadding(int axisTitlePadding);

    int getAxisTitlePadding();

    /**
     * Sets the padding between the tick marks and the plot area
     *
     * @param plotPadding
     */
    void setPlotPadding(int plotPadding);

    int getPlotPadding();

    /**
     * Set the x-axis tick marks and labels visibility
     *
     * @param xAxisTicksVisible
     */
    void setXAxisTicksVisible(boolean xAxisTicksVisible);

    boolean isXAxisTicksVisible();

    /**
     * Set the y-axis tick marks and labels visibility
     *
     * @param yAxisTicksVisible
     */
    void setYAxisTicksVisible(boolean yAxisTicksVisible);

    boolean isYAxisTicksVisible();

    /**
     * Set the x- and y-axis tick label font
     *
     * @param axisTicksFont
     */
    void setAxisTickLabelsFont(Font axisTicksFont);

    Font getAxisTickLabelsFont();

    Color getAxisTickLabelsColor();

    void setAxisTickLabelsColor(Color color);

    /**
     * Set the axis tick mark length
     *
     * @param axisTickMarkLength
     */
    void setAxisTickMarkLength(int axisTickMarkLength);

    int getAxisTickMarkLength();

    /**
     * Sets the padding between the tick labels and the tick marks
     *
     * @param axisTickPadding
     */
    void setAxisTickPadding(int axisTickPadding);

    int getAxisTickPadding();

    void setXAxisLabelRotation(int xAxisLabelRotation);

    int getXAxisLabelRotation();

    void setYAxisLabelRotation(int yAxisLabelRotation);

    int getYAxisLabelRotation();

    /**
     * Sets the visibility of the gridlines on the plot area
     *
     * @param isPlotGridLinesVisible
     */
    void setPlotGridLinesVisible(boolean isPlotGridLinesVisible);

    boolean isPlotGridLinesVisible();

    TextAlignment getXAxisLabelAlignment();

    void setXAxisLabelAlignment(TextAlignment align);

    TextAlignment getYAxisLabelAlignment();

    void setYAxisLabelAlignment(TextAlignment align);

    boolean isAxisTicksLineVisible();

    void setAxisTicksLineVisible(boolean axisTicksLineVisible);

    boolean isAxisTicksMarksVisible();

    void setAxisTicksMarksVisible(boolean axisTicksMarksVisible);

    Color getAxisTickMarksColor();

    void setAxisTickMarksColor(Color axisTickMarksColor);

    Stroke getAxisTickMarksStroke();

    void setAxisTickMarksStroke(Stroke axisTickMarksStroke);

    int getXAxisTickMarkSpacingHint();

    int getYAxisTickMarkSpacingHint();

    boolean isXAxisLogarithmic();

    boolean isYAxisLogarithmic();

}
