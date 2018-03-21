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
package org.clueminer.chart.renderer;

import java.util.List;
import java.util.Set;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.Tick;
import org.clueminer.chart.api.TickType;
import org.clueminer.chart.util.MathUtils;

/**
 *
 * @author deric
 */
public class LinearRenderer2D extends AbstractAxisRenderer2D implements AxisRenderer {

    private static final long serialVersionUID = -8738555725384893517L;

    /**
     * Creates a new renderer for linear axes in two-dimensional space.
     */
    public LinearRenderer2D() {
    }

    /**
     * Converts a world (axis) coordinate value to a view (screen) coordinate
     * value.
     *
     * @param axis Axis
     * @param value World coordinate value to convert
     * @param extrapolate Option to activate extrapolation value that are not
     * on the axis
     * @return Screen coordinate value
     */
    @Override
    public double worldToView(Axis axis, Number value, boolean extrapolate) {
        double min = axis.getMin().doubleValue();
        double max = axis.getMax().doubleValue();
        double val = value.doubleValue();
        if (!extrapolate) {
            if (val <= min) {
                return 0.0;
            }
            if (val >= max) {
                return getShapeLength();
            }
        }
        return (val - min) / (max - min) * getShapeLength();
    }

    /**
     * Converts a view (screen) coordinate value to a world (axis) coordinate
     * value.
     *
     * @param axis Axis
     * @param value View coordinate value to convert
     * @param extrapolate Option to activate extrapolation value that are not
     * on the axis
     * @return World coordinate value
     */
    @Override
    public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
        double min = axis.getMin().doubleValue();
        double max = axis.getMax().doubleValue();
        if (!extrapolate) {
            if (value <= 0.0) {
                return min;
            }
            if (value >= getShapeLength()) {
                return max;
            }
        }
        return value / getShapeLength() * (max - min) + min;
    }

    @Override
    public void createTicks(List<Tick> ticks, Axis axis, double min,
            double max, Set<Double> tickPositions, boolean isAutoSpacing) {
        double tickSpacing = 1.0;
        int ticksMinorCount = 3;
        if (isAutoSpacing) {
            // TODO Use number of screen units to decide whether to subdivide
            double range = max - min;
            // 1-steppings (0.1, 1, 10)
            tickSpacing = MathUtils.magnitude(10.0, range / 4.0);
            // 2-steppings (0.2, 2, 20)
            if (range / tickSpacing > 8.0) {
                tickSpacing *= 2.0;
                ticksMinorCount = 1;
            }
            // 5-steppings (0.5, 5, 50)
            if (range / tickSpacing > 8.0) {
                tickSpacing *= 2.5;
                ticksMinorCount = 4;
            }
        } else {
            tickSpacing = getTickSpacing().doubleValue();
            ticksMinorCount = getMinorTicksCount();
        }

        double tickSpacingMinor = tickSpacing;
        if (ticksMinorCount > 0) {
            tickSpacingMinor = tickSpacing / (ticksMinorCount + 1);
        }

        double minTickMajor = MathUtils.ceil(min, tickSpacing);
        double minTickMinor = MathUtils.ceil(min, tickSpacingMinor);

        int ticksTotal = (int) Math.ceil((max - min) / tickSpacingMinor);
        int initialTicksMinor = (int) ((minTickMajor - min) / tickSpacingMinor);

        // Add major and minor ticks
        // (Use integer to avoid rounding errors)
        for (int tickCur = 0; tickCur < ticksTotal; tickCur++) {
            double tickPositionWorld = minTickMinor + tickCur * tickSpacingMinor;
            if (tickPositions.contains(tickPositionWorld)) {
                continue;
            }
            TickType tickType = TickType.MINOR;
            if ((tickCur - initialTicksMinor) % (ticksMinorCount + 1) == 0) {
                tickType = TickType.MAJOR;
            }
            Tick tick = getTick(tickType, axis, tickPositionWorld);
            if (tick.position != null) {
                ticks.add(tick);
                tickPositions.add(tickPositionWorld);
            }
        }
    }
}
