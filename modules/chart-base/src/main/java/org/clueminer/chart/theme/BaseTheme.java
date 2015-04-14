/*
 * Copyright (C) 2011-2015 clueminer.org
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
import java.awt.Paint;
import java.awt.Stroke;

/**
 *
 * @author deric
 */
public class BaseTheme implements Theme {

    private final ChartTheme chart;

    /**
     * Stroke to draw the plot border.
     */
    private transient Stroke borderStroke;

    private final Paint borderColor;

    public BaseTheme() {
        this.borderColor = Color.BLACK;
        this.chart = new BaseChartTheme();
    }

    @Override
    public Font getFont() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ChartTheme getChart() {
        return chart;
    }

    @Override
    public int getMarkerSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color getErrorBarsColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLegendVisible() {
        return true;
    }

    @Override
    public boolean isErrorBarsColorSeriesColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stroke getBorderStroke() {
        return borderStroke;
    }

    @Override
    public Paint getBorderColor() {
        return borderColor;
    }
}
