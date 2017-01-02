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
package org.clueminer.chart.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

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

    boolean isLegendVisible();

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

}
