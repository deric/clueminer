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
package org.clueminer.chart.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.clueminer.chart.api.TextAlignment;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Location;
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

    private final Color borderColor = Color.BLACK;

    //protected Color bg = new Color(210, 210, 210); //GRAY
    protected Color bg = Color.YELLOW;
    protected Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    protected int chartPadding;
    protected Color chartFontColor;
    protected Font axisTitleFont;
    protected int markerSize = 8;
    protected boolean legendVisible;
    protected boolean xAxisTitleVisible = true;
    protected boolean yAxisTitleVisible = true;
    protected boolean chartTitleVisible = true;
    protected double legendDistance;
    protected int axisTitlePadding;
    protected int plotPadding;
    protected boolean xAxisTicksVisible;
    protected boolean yAxisTicksVisible;
    protected Font axisTicksFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    protected int axisTickMarkLength = 3;
    protected int axisTickPadding = 4;
    protected Location legendPosition = Location.EAST;
    protected Color errorBarsColor = Color.BLACK;
    protected int xAxisLabelRotation = 0;
    protected int yAxisLabelRotation = 0;
    protected boolean plotGridLinesVisible;
    protected int xAxisTickMarkSpacingHint = 74;
    protected int yAxisTickMarkSpacingHint = 44;

    private static final String NAME = "base theme";

    public BaseTheme() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public int getMarkerSize() {
        return markerSize;
    }

    @Override
    public Color getErrorBarsColor() {
        return errorBarsColor;
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

    @Override
    public boolean isLegendVisible() {
        return legendVisible;
    }

    @Override
    public void setLegendVisible(boolean legendVisible) {
        this.legendVisible = legendVisible;
    }

    @Override
    public Location getLegendLocation() {
        return legendPosition;
    }

    @Override
    public void setLegendLocation(Location location) {
        this.legendPosition = location;
    }

    @Override
    public double getLegendDistance() {
        return legendDistance;
    }

    @Override
    public void setLegendDistance(double distance) {
        this.legendDistance = distance;
    }

    @Override
    public boolean isXAxisTitleVisible() {
        return xAxisTitleVisible;
    }

    @Override
    public void setXAxisTitleVisible(boolean xAxisTitleVisible) {
        this.xAxisTitleVisible = xAxisTitleVisible;
    }

    @Override
    public boolean isYAxisTitleVisible() {
        return yAxisTitleVisible;
    }

    @Override
    public void setYAxisTitleVisible(boolean yAxisTitleVisible) {
        this.yAxisTitleVisible = yAxisTitleVisible;
    }

    @Override
    public void setChartTitleVisible(boolean isChartTitleVisible) {
        this.chartTitleVisible = isChartTitleVisible;
    }

    @Override
    public boolean isChartTitleVisible() {
        return chartTitleVisible;
    }

    @Override
    public void setAxisTitlePadding(int axisTitlePadding) {
        this.axisTitlePadding = axisTitlePadding;
    }

    @Override
    public int getAxisTitlePadding() {
        return axisTitlePadding;
    }

    @Override
    public void setPlotPadding(int plotPadding) {
        this.plotPadding = plotPadding;
    }

    @Override
    public int getPlotPadding() {
        return plotPadding;
    }

    @Override
    public void setXAxisTicksVisible(boolean xAxisTicksVisible) {
        this.xAxisTicksVisible = xAxisTicksVisible;
    }

    @Override
    public boolean isXAxisTicksVisible() {
        return xAxisTicksVisible;
    }

    @Override
    public void setYAxisTicksVisible(boolean yAxisTicksVisible) {
        this.yAxisTicksVisible = yAxisTicksVisible;
    }

    @Override
    public boolean isYAxisTicksVisible() {
        return yAxisTicksVisible;
    }

    @Override
    public void setAxisTickLabelsFont(Font axisTicksFont) {
        this.axisTicksFont = axisTicksFont;
    }

    @Override
    public Font getAxisTickLabelsFont() {
        return axisTicksFont;
    }

    @Override
    public void setAxisTickMarkLength(int axisTickMarkLength) {
        this.axisTickMarkLength = axisTickMarkLength;
    }

    @Override
    public int getAxisTickMarkLength() {
        return axisTickMarkLength;
    }

    @Override
    public void setAxisTickPadding(int axisTickPadding) {
        this.axisTickPadding = axisTickPadding;
    }

    @Override
    public int getAxisTickPadding() {
        return axisTickPadding;
    }

    @Override
    public void setXAxisLabelRotation(int xAxisLabelRotation) {
        this.xAxisLabelRotation = xAxisLabelRotation;
    }

    @Override
    public int getXAxisLabelRotation() {
        return xAxisLabelRotation;
    }

    @Override
    public void setYAxisLabelRotation(int yAxisLabelRotation) {
        this.yAxisLabelRotation = yAxisLabelRotation;
    }

    @Override
    public int getYAxisLabelRotation() {
        return yAxisLabelRotation;
    }

    @Override
    public void setPlotGridLinesVisible(boolean isPlotGridLinesVisible) {
        this.plotGridLinesVisible = isPlotGridLinesVisible;
    }

    @Override
    public boolean isPlotGridLinesVisible() {
        return plotGridLinesVisible;
    }

    @Override
    public Color getAxisTickLabelsColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAxisTickLabelsColor(Color color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TextAlignment getXAxisLabelAlignment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setXAxisLabelAlignment(TextAlignment align) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TextAlignment getYAxisLabelAlignment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setYAxisLabelAlignment(TextAlignment align) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAxisTicksLineVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAxisTicksLineVisible(boolean axisTicksLineVisible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAxisTicksMarksVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAxisTicksMarksVisible(boolean axisTicksMarksVisible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color getAxisTickMarksColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAxisTickMarksColor(Color axisTickMarksColor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stroke getAxisTickMarksStroke() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAxisTickMarksStroke(Stroke axisTickMarksStroke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getXAxisTickMarkSpacingHint() {
        return xAxisTickMarkSpacingHint;
    }

    @Override
    public int getYAxisTickMarkSpacingHint() {
        return yAxisTickMarkSpacingHint;
    }

    @Override
    public boolean isXAxisLogarithmic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isYAxisLogarithmic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
