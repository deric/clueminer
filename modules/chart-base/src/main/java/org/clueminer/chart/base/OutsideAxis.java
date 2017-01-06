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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.AxisType;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Label;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Location;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class OutsideAxis extends AbstractAxis implements Axis, Serializable {

    private double min;

    private double max;

    private AxisType axisType;

    /** the axis title */
    private AxisTitle axisTitle;

    /** the axis tick */
    private AxisTick axisTick;

    public OutsideAxis(Plot parent, AxisRenderer renderer, Orientation orient) {
        this(true);
        this.plot = parent;
        this.renderer = renderer;
        this.orientation = orient;
        this.axisTitle = new AxisTitle(this);
        this.axisTick = new AxisTick(this);
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

    /**
     * Reset the default min and max values in preparation for calculating the actual min and max
     */
    public void resetMinMax() {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    @Override
    public void draw(DrawingContext context) {

        Graphics2D g = context.getGraphics();
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
            double yOffset = plot.getTitle().getSizeHint();
            double width = 80; // arbitrary, final width depends on Axis tick labels

            double chartLegendWidth = 0;
            if (theme.getLegendLocation() == Location.EAST_OUTSIDE) {
                chartLegendWidth = plot.getLegend().getSizeHint(g)[0];
            }

            double approximateXAxisWidth
                    = plot.getWidth()
                    - width // y-axis approx. width

                    - chartLegendWidth
                    - 2
                    * theme.getChartPadding()
                    - (theme.isYAxisTicksVisible() ? (theme.getPlotPadding()) : 0)
                    - (theme.getLegendLocation() == Location.EAST_OUTSIDE && theme.isLegendVisible() ? theme.getChartPadding() : 0);

            double height
                    = plot.getHeight() - yOffset - plot.getAxis(AxisPosition.X).getHeightHint(approximateXAxisWidth) - theme.getPlotPadding()
                    - theme.getChartPadding();
            Rectangle2D yAxisRectangle = new Rectangle2D.Double(xOffset, yOffset, width, height);
            this.paintZone = yAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(yAxisRectangle);

            // fill in Axis with sub-components
            axisTitle.draw(context);
            axisTick.draw(context);

            xOffset = paintZone.getX();
            yOffset = paintZone.getY();
            width = (theme.isYAxisTitleVisible() ? axisTitle.getBounds().getWidth() : 0) + axisTick.getBounds().getWidth();
            height = paintZone.getHeight();
            bounds = new Rectangle2D.Double(xOffset, yOffset, width, height);

            // g.setColor(Color.yellow);
            // g.draw(bounds);
        } else { // X-Axis

            // calculate paint zone
            // |____________________|
            double xOffset
                    = plot.getAxis(AxisPosition.Y).getBounds().getWidth() + (theme.isYAxisTicksVisible() ? theme.getPlotPadding() : 0)
                    + theme.getChartPadding();
            double yOffset = plot.getAxis(AxisPosition.Y).getBounds().getY() + plot.getAxis(AxisPosition.Y).getBounds().getHeight() + theme.getPlotPadding();

            double chartLegendWidth = 0;
            if (theme.getLegendLocation() == Location.EAST_OUTSIDE) {
                chartLegendWidth = plot.getLegend().getSizeHint(g)[0];
            }

            double width
                    = plot.getWidth()
                    - plot.getAxis(AxisPosition.Y).getBounds().getWidth() // y-axis was already painted

                    - chartLegendWidth
                    - 2
                    * theme.getChartPadding()
                    - (theme.isYAxisTicksVisible() ? (theme.getPlotPadding()) : 0)
                    - (theme.getLegendLocation() == Location.EAST_OUTSIDE && theme.isLegendVisible() ? theme.getChartPadding() : 0);

            // double height = this.getXAxisHeightHint(width);
            // System.out.println("height: " + height);
            // the Y-Axis was already draw at this point so we know how much vertical room is left for the X-Axis
            double height
                    = plot.getHeight() - plot.getAxis(AxisPosition.Y).getBounds().getY()
                    - plot.getAxis(AxisPosition.Y).getBounds().getHeight() - theme.getChartPadding()
                    - theme.getPlotPadding();
            // System.out.println("height2: " + height2);

            Rectangle2D xAxisRectangle = new Rectangle2D.Double(xOffset, yOffset, width, height);

            // the paint zone
            this.paintZone = xAxisRectangle;
            // g.setColor(Color.green);
            // g.draw(xAxisRectangle);

            // now paint the X-Axis given the above paint zone
            axisTitle.draw(context);
            axisTick.draw(context);

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
        //TODO: check min & max
        return true;
    }

    /**
     * The vertical Y-Axis is drawn first, but to know the lower bounds of it,
     * we need to know how high the X-Axis paint zone is going to be. Since the
     * tick labels could be rotated, we need to actually determine the tick
     * labels first to get an idea of how tall the X-Axis tick labels will be.
     *
     * @param workingSpace
     * @return
     */
    public double getHeightHint(double workingSpace) {
        Theme theme = plot.getTheme();

        // Axis title
        double titleHeight = 0.0;
        if (axisTitle.getText() != null && !axisTitle.getText().trim().equalsIgnoreCase("") && theme.isXAxisTitleVisible()) {
            TextLayout textLayout = new TextLayout(axisTitle.getText(), theme.getAxisTitleFont(), new FontRenderContext(null, true, false));
            Rectangle2D rectangle = textLayout.getBounds();
            titleHeight = rectangle.getHeight() + theme.getAxisTitlePadding();
        }

        // Axis tick labels
        double axisTickLabelsHeight = 0.0;
        if (theme.isXAxisTicksVisible()) {

            // get some real tick labels
            AxisTickCalculator axisTickCalculator = axisTick.getAxisTickCalculator(plot, workingSpace);
            String sampleLabel = axisTickCalculator.getTickLabels().get(0);
            // find the longest String in all the labels
            for (int i = 1; i < axisTickCalculator.getTickLabels().size(); i++) {
                if (axisTickCalculator.getTickLabels().get(i) != null && axisTickCalculator.getTickLabels().get(i).length() > sampleLabel.length()) {
                    sampleLabel = axisTickCalculator.getTickLabels().get(i);
                }
            }

            TextLayout textLayout = new TextLayout(sampleLabel, theme.getAxisTickLabelsFont(), new FontRenderContext(null, true, false));
            AffineTransform rot
                    = theme.getXAxisLabelRotation() == 0 ? null : AffineTransform.getRotateInstance(-1 * Math.toRadians(theme.getXAxisLabelRotation()));
            Shape shape = textLayout.getOutline(rot);
            Rectangle2D rectangle = shape.getBounds();
            axisTickLabelsHeight = rectangle.getHeight() + theme.getAxisTickPadding() + theme.getAxisTickMarkLength();
        }
        return titleHeight + axisTickLabelsHeight;
    }

    @Override
    public Label getTitle() {
        return axisTitle;
    }

}
