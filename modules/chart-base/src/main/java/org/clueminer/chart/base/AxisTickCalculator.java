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
package org.clueminer.chart.base;

import java.util.LinkedList;
import java.util.List;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.ChartType;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.MathUtils;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public abstract class AxisTickCalculator {

    /** the List of tick label position in pixels */
    protected List<Double> tickLocations = new LinkedList<>();

    /** the List of tick label values */
    protected List<String> tickLabels = new LinkedList<>();

    protected final Orientation axisDirection;

    protected final double workingSpace;

    protected final double minValue;

    protected final double maxValue;

    protected final Plot plot;

    /**
     * Constructor
     *
     * @param axisDirection
     * @param workingSpace
     * @param minValue
     * @param maxValue
     * @param plot
     */
    public AxisTickCalculator(Orientation axisDirection, double workingSpace, double minValue, double maxValue, Plot plot) {
        this.plot = plot;
        Theme theme = plot.getTheme();

        // override min/max value for bar charts' Y-Axis
        double overrideMinValue = minValue;
        double overrideMaxValue = maxValue;
        if (plot.getChartType() == ChartType.Bar && axisDirection == Orientation.VERTICAL) { // this is the Y-Axis for a bar chart
            if (minValue > 0.0 && maxValue > 0.0) {
                overrideMinValue = 0.0;
            }
            if (minValue < 0.0 && maxValue < 0.0) {
                overrideMaxValue = 0.0;
            }
        }

        if (plot.getChartType() == ChartType.Bar && theme.isYAxisLogarithmic()) {
            int logMin = (int) Math.floor(Math.log10(minValue));
            overrideMinValue = MathUtils.pow(10, logMin);
        }

        Axis axisX = plot.getAxis(AxisPosition.X);
        Axis axisY = plot.getAxis(AxisPosition.Y);

        // override min and maxValue if specified
        if (axisDirection == Orientation.HORIZONTAL && axisX.getMin() != null && plot.getChartType() != ChartType.Bar) {
            overrideMinValue = axisX.getMin().doubleValue();
        }
        if (axisDirection == Orientation.VERTICAL && axisY.getMin() != null) {
            overrideMinValue = axisY.getMin().doubleValue();
        }
        if (axisDirection == Orientation.HORIZONTAL && axisX.getMax() != null && plot.getChartType() != ChartType.Bar) {
            overrideMaxValue = axisX.getMax().doubleValue();
        }
        if (axisDirection == Orientation.VERTICAL && axisY.getMax() != null) {
            overrideMaxValue = axisY.getMax().doubleValue();
        }
        this.axisDirection = axisDirection;
        this.workingSpace = workingSpace;
        this.minValue = overrideMinValue;
        this.maxValue = overrideMaxValue;
    }

    /**
     * Gets the first position
     *
     * @param gridStep
     * @return
     */
    double getFirstPosition(double gridStep) {
        double firstPosition = minValue - (minValue % gridStep) - gridStep;
        return firstPosition;
    }

    public List<Double> getTickLocations() {
        return tickLocations;
    }

    public List<String> getTickLabels() {
        return tickLabels;
    }

    /**
     * Determine the grid step for the data set given the space in pixels allocated for the axis
     *
     * @param tickSpace in plot space
     * @return
     */
    public double getNumericalGridStep(double tickSpace) {

        // this prevents an infinite loop when the plot gets sized really small.
        if (tickSpace < 10) {
            return 1.0;
        }

        // the span of the data
        double span = Math.abs(maxValue - minValue); // in data space

        int tickMarkSpaceHint = (axisDirection == Orientation.HORIZONTAL
                                 ? plot.getTheme().getXAxisTickMarkSpacingHint() : plot.getTheme().getYAxisTickMarkSpacingHint());

        // for very short plots, squeeze some more ticks in than normal
        if (axisDirection == Orientation.VERTICAL && tickSpace < 160) {
            tickMarkSpaceHint = 25;
        }

        double gridStepHint = span / tickSpace * tickMarkSpaceHint;

        // gridStepHint --> significand * 10 ** exponent
        // e.g. 724.1 --> 7.241 * 10 ** 2
        double significand = gridStepHint;
        int exponent = 0;
        if (significand == 0) {
            exponent = 1;
        } else if (significand < 1) {
            while (significand < 1) {
                significand *= 10.0;
                exponent--;
            }
        } else {
            while (significand >= 10 || significand == Double.NEGATIVE_INFINITY) {
                significand /= 10.0;
                exponent++;
            }
        }

        // calculate the grid step with hint.
        double gridStep;
        if (significand > 7.5) {
            // gridStep = 10.0 * 10 ** exponent
            gridStep = 10.0 * MathUtils.pow(10, exponent);
        } else if (significand > 3.5) {
            // gridStep = 5.0 * 10 ** exponent
            gridStep = 5.0 * MathUtils.pow(10, exponent);
        } else if (significand > 1.5) {
            // gridStep = 2.0 * 10 ** exponent
            gridStep = 2.0 * MathUtils.pow(10, exponent);
        } else {
            // gridStep = 1.0 * 10 ** exponent
            gridStep = MathUtils.pow(10, exponent);
        }
        return gridStep;
    }

}
