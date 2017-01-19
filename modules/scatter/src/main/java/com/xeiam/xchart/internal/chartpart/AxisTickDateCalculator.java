/**
 * Copyright 2011 - 2015 Xeiam LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xeiam.xchart.internal.chartpart;

import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.internal.Utils;
import com.xeiam.xchart.internal.chartpart.Axis.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis ticks for date axes
 *
 * @author timmolter
 */
public class AxisTickDateCalculator extends AxisTickCalculator {

    DateFormatter dateFormatter;
    private static final Logger LOG = LoggerFactory.getLogger(AxisTickDateCalculator.class);

    /**
     * Constructor
     *
     * @param axisDirection
     * @param workingSpace
     * @param minValue
     * @param maxValue
     * @param styleManager
     */
    public AxisTickDateCalculator(Direction axisDirection, double workingSpace, double minValue, double maxValue, StyleManager styleManager) {
        super(axisDirection, workingSpace, minValue, maxValue, styleManager);
        dateFormatter = new DateFormatter(styleManager);
        calculate();
    }

    private void calculate() {
        //sanity checks
        if (minValue == maxValue) {
            throw new IllegalArgumentException("Min and Max can't have the same value: " + minValue);
        }

        // tick space - a percentage of the working space available for ticks
        double tickSpace = Math.abs(styleManager.getAxisTickSpacePercentage() * workingSpace); // in plot space

        // where the tick should begin in the working space in pixels
        double margin = Utils.getTickStartOffset(workingSpace, tickSpace); // in plot space double gridStep = getGridStepForDecimal(tickSpace);

        // the span of the data
        long span = (long) Math.abs(maxValue - minValue); // in data space
        LOG.trace("span = {}, tick space = {}", span, tickSpace);

        long gridStepHint = (span / (long) tickSpace * styleManager.getXAxisTickMarkSpacingHint());
        LOG.trace("grid step hint = {}", gridStepHint);

        long timeUnit = dateFormatter.getTimeUnit(gridStepHint);

        double gridStep = 0.0;
        int[] steps = dateFormatter.getValidTickStepsMap().get(timeUnit);
        LOG.trace("time unit = {}, {} steps", timeUnit, steps.length);
        for (int i = 0; i < steps.length - 1; i++) {
            if (gridStepHint < (timeUnit * steps[i] + timeUnit * steps[i + 1]) / 2.0) {
                gridStep = timeUnit * steps[i];
                break;
            }
        }

        double firstPosition = getFirstPosition(gridStep);

        // generate all tickLabels and tickLocations from the first to last position
        for (double value = firstPosition; value <= maxValue + 2 * gridStep; value = value + gridStep) {

            tickLabels.add(dateFormatter.formatDate(value, timeUnit));
            // here we convert tickPosition finally to plot space, i.e. pixels
            double tickLabelPosition = margin + ((value - minValue) / (maxValue - minValue) * tickSpace);
            tickLocations.add(tickLabelPosition);
        }
    }

}
