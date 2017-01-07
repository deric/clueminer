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
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class AxisTick extends AbstractDrawable implements Drawable {

    /** parent */
    private final Axis axis;

    private final AxisTickLabels axisTickLabels;

    private final AxisTickMarks axisTickMarks;

    AxisTickCalculator axisTickCalculator = null;

    /**
     * Constructor
     *
     * @param axis
     */
    protected AxisTick(Axis axis) {
        this.axis = axis;
        axisTickLabels = new AxisTickLabels(this);
        axisTickMarks = new AxisTickMarks(this);
    }

    @Override
    public void draw(DrawingContext context) {
        Graphics2D g = context.getGraphics();
        Plot plot = axis.getPlot();
        Theme theme = axis.getPlot().getTheme();

        double workingSpace = 0.0;
        // Y-Axis
        if (axis.getOrientation() == Orientation.VERTICAL) {
            workingSpace = axis.getPaintZone().getHeight(); // number of pixels the axis has to work with for drawing AxisTicks
        } // X-Axis
        else if (axis.getOrientation() == Orientation.HORIZONTAL) {
            workingSpace = axis.getPaintZone().getWidth(); // number of pixels the axis has to work with for drawing AxisTicks
        }

        axisTickCalculator = getAxisTickCalculator(plot, workingSpace);

        if (axis.getOrientation() == Orientation.VERTICAL && theme.isYAxisTicksVisible()) {

            axisTickLabels.draw(context);
            axisTickMarks.draw(context);

            bounds = new Rectangle2D.Double(
                    axisTickLabels.getBounds().getX(),
                    axisTickLabels.getBounds().getY(),
                    axisTickLabels.getBounds().getWidth() + theme.getAxisTickPadding() + axisTickMarks.getBounds().getWidth(),
                    axisTickMarks.getBounds().getHeight()
            );

            // g.setColor(Color.red);
            // g.draw(bounds);
        } else if (axis.getOrientation() == Orientation.HORIZONTAL && theme.isXAxisTicksVisible()) {

            axisTickLabels.draw(context);
            axisTickMarks.draw(context);

            bounds
                    = new Rectangle2D.Double(axisTickMarks.getBounds().getX(), axisTickMarks.getBounds().getY(), axisTickLabels.getBounds().getWidth(), axisTickMarks.getBounds().getHeight()
                            + theme.getAxisTickPadding() + axisTickLabels.getBounds().getHeight());
            // g.setColor(Color.red);
            // g.draw(bounds);

        }

    }

    public AxisTickCalculator getAxisTickCalculator(Plot plot, double workingSpace) {

        //TODO: implement appropriate classes
        Theme theme = plot.getTheme();

        /* if (axis.getOrientation() == Orientation.HORIZONTAL && plot.getChartType() == ChartType.Bar) {

            return new AxisTickBarChartCalculator(axis.getOrientation(), workingSpace, axis.getMin(), axis.getMax(), getChartPainter());

        } else if (axis.getOrientation() == Orientation.HORIZONTAL && theme.isXAxisLogarithmic() && axis.getAxisType() != AxisType.Date) {

            return new AxisTickLogarithmicCalculator(axis.getOrientation(), workingSpace, axis.getMin(), axis.getMax(), theme);

        } else if (axis.getOrientation() == Orientation.VERTICAL && theme.isYAxisLogarithmic() && axis.getAxisType() != AxisType.Date) {

            return new AxisTickLogarithmicCalculator(axis.getOrientation(), workingSpace, axis.getMin(), axis.getMax(), theme);

        } else if (axis.getOrientation() == AxisType.Date) {

            return new AxisTickDateCalculator(axis.getOrientation(), workingSpace, axis.getMin(), axis.getMax(), theme);

        } else { // number

            return new AxisTickNumericalCalculator(axis.getOrientation(), workingSpace, axis.getMin(), axis.getMax(), theme);

        } */
        return null;
    }

    // Getters /////////////////////////////////////////////////
    public Axis getAxis() {

        return axis;
    }

    public AxisTickLabels getAxisTickLabels() {

        return axisTickLabels;
    }

    public List<Double> getTickLocations() {

        return axisTickCalculator.getTickLocations();
    }

    public List<String> getTickLabels() {

        return axisTickCalculator.getTickLabels();
    }

}
