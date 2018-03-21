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
package org.clueminer.chart.api;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import org.clueminer.chart.data.DataSource;

/**
 * <p>
 * Interface for classes that display data in a plot.</p>
 * <p>
 * Functionality includes:</p>
 * <ul>
 * <li>Adding axes to the plot</li>
 * <li>Adding a title to the plot</li>
 * <li>Adding a legend to the plot</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public interface Plot extends Drawable, Container {

    /**
     * Returns the axis with the specified name.
     *
     * @param name Name of the axis.
     * @return Axis.
     */
    Axis getAxis(AxisPosition name);

    /**
     * Sets the axis with the specified name and the associated
     * {@code AxisRenderer}.
     *
     * @param name Name of the axis.
     * @param axis Axis.
     */
    void setAxis(AxisPosition name, Axis axis);

    /**
     * Removes the axis with the specified name.
     *
     * @param name Name of the axis to be removed.
     */
    void removeAxis(AxisPosition name);

    /**
     * Returns a collection of all names of the axes stored in this plot.
     *
     * @return The names of all axes stored in this plot.
     */
    Collection<AxisPosition> getAxesNames();

    /**
     * Tries to automatically set the ranges of the axes specified by the name
     * if it is set to auto-scale.
     *
     * @param axisName Name of the axis that should be scaled.
     * @see Axis#setAutoscaled(boolean)
     */
    void autoscaleAxis(AxisPosition axisName);

    /**
     * Returns the renderer for the axis with the specified name.
     *
     * @param axisName Axis name.
     * @return Instance that renders the axis.
     */
    AxisRenderer getAxisRenderer(AxisPosition axisName);

    /**
     * Sets the renderer for the axis with the specified name.
     *
     * @param axisName Name of the axis to be rendered.
     * @param renderer Instance to render the axis.
     */
    void setAxisRenderer(AxisPosition axisName, AxisRenderer renderer);

    /**
     * Returns the drawing area of this plot.
     *
     * @return {@code PlotArea2D}.
     */
    Rectangle2D getPlotArea();

    /**
     * Returns the title component of this plot.
     *
     * @return Label representing the title.
     */
    Label getTitle();

    /**
     * Returns the legend component.
     *
     * @return Legend.
     */
    Legend getLegend();

    /**
     * Adds a new data series to the plot which is visible by default.
     *
     * @param source Data series.
     */
    void add(DataSource source);

    /**
     * Adds a new data series to the plot.
     *
     * @param source  Data series.
     * @param visible {@code true} if the series should be displayed,
     *                {@code false} otherwise.
     */
    void add(DataSource source, boolean visible);

    /**
     * Inserts the specified data series to the plot at a specified position.
     *
     * @param index   Position.
     * @param source  Data series.
     * @param visible {@code true} if the series should be displayed,
     *                {@code false} otherwise.
     */
    void add(int index, DataSource source, boolean visible);

    /**
     * Returns whether the plot contains the specified data series.
     *
     * @param source Data series.
     * @return {@code true} if the specified element is stored in the
     *         plot, otherwise {@code false}
     */
    boolean contains(DataSource source);

    /**
     * Returns the data series at a specified index.
     *
     * @param index Position of the data series.
     * @return Instance of the data series.
     */
    DataSource get(int index);

    /**
     * Deletes the specified data series from the plot.
     *
     * @param source Data series.
     * @return {@code true} if the series existed,
     *         otherwise {@code false}.
     */
    boolean remove(DataSource source);

    /**
     * Removes all data series from this plot.
     */
    void clear();

    /**
     * Returns the mapping of data source columns to axis names. The elements
     * of returned array equal the column indexes, i.e. the first element (axis
     * name) matches the first column of {@code source}. If no mapping exists
     * {@code null} will be stored in the array.
     *
     * @param source Data source.
     * @return Array containing axis names in the order of the columns,
     *         or {@code null} if no mapping exists for the column.
     */
    String[] getMapping(DataSource source);

    /**
     * Sets the mapping of data source columns to axis names. The column index
     * is taken from the order of the axis names, i.e. the first column of
     * {@code source} will be mapped to first element of {@code axisNames}.
     * Axis names with value {@code null} will be ignored.
     *
     * @param source    Data source.
     * @param axisNames Sequence of axis names in the order of the columns.
     */
    void setMapping(DataSource source, String... axisNames);

    /**
     * Returns a list of all data series stored in the plot.
     *
     * @return List of all data series.
     */
    List<DataSource> getData();

    /**
     * Returns a list of all visible data series stored in the plot.
     *
     * @return List of all visible data series.
     */
    List<DataSource> getVisibleData();

    /**
     * Returns whether the specified data series is drawn.
     *
     * @param source Data series.
     * @return {@code true} if visible, {@code false} otherwise.
     */
    boolean isVisible(DataSource source);

    /**
     * Changes the visibility of the specified data series.
     *
     * @param source  Data series.
     * @param visible {@code true} if the series should be visible,
     *                {@code false} otherwise.
     */
    void setVisible(DataSource source, boolean visible);

    /**
     * Returns style used for plot rendering.
     *
     * @return plot's theme
     */
    Theme getTheme();

    /**
     * Sets the theme for plot rendering.
     *
     * @param t theme
     */
    void setTheme(Theme t);

    /**
     * @return plot type
     */
    ChartType getChartType();



}
