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
package org.clueminer.chart.base;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.theme.Theme;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class OutsideAxis extends AbstractAxis implements Axis, Serializable {

    private double min;

    private double max;

    /** the paint zone */
    private Rectangle2D paintZone;

    private Plot plot;

    public OutsideAxis(Plot parent, AxisRenderer renderer, Orientation orient) {
        this(true);
        this.plot = parent;
        this.renderer = renderer;
        this.orientation = orient;
    }

    /**
     * Initializes a new instance with a specified automatic scaling mode, but
     * without minimum and maximum values.
     *
     * @param autoscaled {@code true} to turn automatic scaling on
     */
    private OutsideAxis(boolean autoscaled) {
        axisListeners = new LinkedList<>();
        this.autoscaled = autoscaled;
    }

    public OutsideAxis(AxisRenderer renderer, Orientation orient) {
        this(true);
        this.renderer = renderer;
        this.orientation = orient;
    }

    /**
     * Reset the default min and max values in preparation for calculating the actual min and max
     */
    public void resetMinMax() {

        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    @Override
    public void draw(DrawingContext context) {

        paintZone = new Rectangle2D.Double();
        Theme theme = plot.getTheme();

        // determine Axis bounds
        if (orientation == Orientation.HORIZONTAL) { // Y-Axis - gets called first

            // first determine the height of
            // calculate paint zone
            // ----
            // |
            // |
            // |
            // |
            // ----
            double xOffset = theme.getChartPadding();
            double yOffset = getChartPainter().getChartTitle().getSizeHint();
            double width = 80; // arbitrary, final width depends on Axis tick labels

            double chartLegendWidth = 0;
            if (getChartPainter().getStyleManager().getLegendPosition() == LegendPosition.OutsideE) {
                chartLegendWidth = getChartPainter().getChartLegend().getSizeHint(g)[0];
            }

            double approximateXAxisWidth
                    = getChartPainter().getWidth()
                    - width // y-axis approx. width

                    - chartLegendWidth
                    - 2
                    * getChartPainter().getStyleManager().getChartPadding()
                    - (getChartPainter().getStyleManager().isYAxisTicksVisible() ? (getChartPainter().getStyleManager().getPlotPadding()) : 0)
                    - (getChartPainter().getStyleManager().getLegendPosition() == LegendPosition.OutsideE && getChartPainter().getStyleManager().isLegendVisible() ? getChartPainter().getStyleManager()
                    .getChartPadding() : 0);

            double height
                    = getChartPainter().getHeight() - yOffset - axisPair.getXAxis().getXAxisHeightHint(approximateXAxisWidth) - getChartPainter().getStyleManager().getPlotPadding()
                    - getChartPainter().getStyleManager().getChartPadding();
            Rectangle2D yAxisRectangle = new Rectangle2D.Double(xOffset, yOffset, width, height);
            this.paintZone = yAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(yAxisRectangle);

            // fill in Axis with sub-components
            axisTitle.paint(g);
            axisTick.paint(g);

            xOffset = paintZone.getX();
            yOffset = paintZone.getY();
            width = (getChartPainter().getStyleManager().isYAxisTitleVisible() ? axisTitle.getBounds().getWidth() : 0) + axisTick.getBounds().getWidth();
            height = paintZone.getHeight();
            bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);

            // g.setColor(Color.yellow);
            // g.draw(bounds);
        } else { // X-Axis

            // calculate paint zone
            // |____________________|
            double xOffset
                    = axisPair.getYAxis().getBounds().getWidth() + (getChartPainter().getStyleManager().isYAxisTicksVisible() ? getChartPainter().getStyleManager().getPlotPadding() : 0)
                    + getChartPainter().getStyleManager().getChartPadding();
            double yOffset = axisPair.getYAxis().getBounds().getY() + axisPair.getYAxis().getBounds().getHeight() + getChartPainter().getStyleManager().getPlotPadding();

            double chartLegendWidth = 0;
            if (getChartPainter().getStyleManager().getLegendPosition() == LegendPosition.OutsideE) {
                chartLegendWidth = getChartPainter().getChartLegend().getSizeHint(g)[0];
            }

            double width
                    = getChartPainter().getWidth()
                    - axisPair.getYAxis().getBounds().getWidth() // y-axis was already painted

                    - chartLegendWidth
                    - 2
                    * getChartPainter().getStyleManager().getChartPadding()
                    - (getChartPainter().getStyleManager().isYAxisTicksVisible() ? (getChartPainter().getStyleManager().getPlotPadding()) : 0)
                    - (getChartPainter().getStyleManager().getLegendPosition() == LegendPosition.OutsideE && getChartPainter().getStyleManager().isLegendVisible() ? getChartPainter().getStyleManager()
                    .getChartPadding() : 0);

            // double height = this.getXAxisHeightHint(width);
            // System.out.println("height: " + height);
            // the Y-Axis was already draw at this point so we know how much vertical room is left for the X-Axis
            double height
                    = getChartPainter().getHeight() - axisPair.getYAxis().getBounds().getY() - axisPair.getYAxis().getBounds().getHeight() - getChartPainter().getStyleManager().getChartPadding()
                    - getChartPainter().getStyleManager().getPlotPadding();
            // System.out.println("height2: " + height2);

            Rectangle2D xAxisRectangle = new Rectangle2D.Double(xOffset, yOffset, width, height);

            // the paint zone
            this.paintZone = xAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(xAxisRectangle);

            // now paint the X-Axis given the above paint zone
            axisTitle.paint(g);
            axisTick.paint(g);

            setBounds(paintZone);

            // g.setColor(Color.yellow);
            // g.draw(bounds);
        }
    }

    @Override
    public Number getMin() {
        return min;
    }

    @Override
    public void setMin(Number min) {
        this.min = (double) min;
    }

    @Override
    public Number getMax() {
        return max;
    }

    @Override
    public void setMax(Number max) {
        this.max = (double) max;
    }

    @Override
    public double getRange() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRange(Number min, Number max) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
