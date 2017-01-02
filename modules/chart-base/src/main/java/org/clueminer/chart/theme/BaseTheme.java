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
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Theme.class)
public class BaseTheme implements Theme {

    /**
     * Stroke to draw the plot border.
     */
    private transient Stroke borderStroke;

    private final Color borderColor;

    protected Color bg = new Color(210, 210, 210); //GRAY
    protected Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    protected int chartPadding;
    protected Color chartFontColor;
    protected Font axisTitleFont;

    private static final String name = "base theme";

    public BaseTheme() {
        this.borderColor = Color.BLACK;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Font getFont() {
        return font;
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
    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public Color getBackground() {
        return bg;
    }

    @Override
    public void setBackground(Color background) {
        this.bg = background;
    }

    @Override
    public void setBorderStroke(Stroke border) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBorderColor(Color color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Set the chart padding
     *
     * @param chartPadding
     */
    @Override
    public void setChartPadding(int chartPadding) {
        this.chartPadding = chartPadding;
    }

    @Override
    public int getChartPadding() {
        return chartPadding;
    }

    @Override
    public void setChartFontColor(Color color) {
        this.chartFontColor = color;
    }

    @Override
    public Color getChartFontColor() {
        return chartFontColor;
    }

    /**
     * Set the x- and y-axis title font
     *
     * @param axisTitleFont
     */
    @Override
    public void setAxisTitleFont(Font axisTitleFont) {
        this.axisTitleFont = axisTitleFont;
    }

    @Override
    public Font getAxisTitleFont() {
        return axisTitleFont;
    }

}
