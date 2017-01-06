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
package org.clueminer.chart.api;

import org.clueminer.chart.util.Orientation;

/**
 * <p>
 * Class that represents an arbitrary axis.</p>
 * <p>
 * Functionality includes:</p>
 * <ul>
 * <li>Different ways of setting and getting the range of this axis</li>
 * <li>Administration of {@link AxisListener AxisListeners}</li>
 * </ul>
 */
public interface Axis extends Drawable {

    /**
     * Adds the specified {@code AxisListener} to this Axis. The Listeners will
     * be notified if changes to the Axis occur, for Example if the minimum or
     * maximum value changes.
     *
     * @param listener Listener to be added
     * @see AxisListener
     */
    void addAxisListener(AxisListener listener);

    /**
     * Removes the specified {@code AxisListener} from this Axis.
     *
     * @param listener Listener to be removed
     * @see AxisListener
     */
    void removeAxisListener(AxisListener listener);

    /**
     * Notifies all registered {@code AxisListener}s that the value range has
     * changed.
     *
     * @param min new minimum value
     * @param max new maximum value
     */
    void fireRangeChanged(Number min, Number max);

    /**
     * Returns the minimum value to be displayed.
     *
     * @return Minimum value.
     */
    Number getMin();

    /**
     * Sets the minimum value to be displayed.
     *
     * @param min Minimum value.
     */
    void setMin(Number min);

    /**
     * Returns the maximum value to be displayed.
     *
     * @return Maximum value.
     */
    Number getMax();

    /**
     * Sets the maximum value to be displayed.
     *
     * @param max Maximum value.
     */
    void setMax(Number max);

    /**
     * Returns the range of values to be displayed.
     *
     * @return Distance between maximum and minimum value.
     */
    double getRange();

    /**
     * Sets the range of values to be displayed.
     *
     * @param min Minimum value.
     * @param max Maximum value.
     */
    void setRange(Number min, Number max);

    /**
     * Returns whether the axis range should be determined automatically rather
     * than using the axis's minimum and a maximum values.
     *
     * @return whether the axis is scaled automatically to fit the current data
     */
    boolean isAutoscaled();

    /**
     * Sets whether the axis range should be determined automatically rather
     * than using the axis's minimum and a maximum values.
     *
     * @param autoscaled Defines whether the axis should be automatically scaled
     *                   to fit the current data.
     */
    void setAutoscaled(boolean autoscaled);

    /**
     * Returns whether the currently set minimum and maximum values are valid.
     *
     * @return {@code true} when minimum and maximum values are correct,
     *         otherwise {@code false}
     */
    boolean isValid();

    AxisRenderer getRenderer();

    /**
     * Set axis renderer
     *
     * @param renderer
     */
    void setRenderer(AxisRenderer renderer);

    Orientation getOrientation();

    void setOrientation(Orientation orientation);

    /**
     * Returns plot to which axis belong
     *
     * @return parent plot
     */
    Plot getPlot();

    double getHeightHint(double workingSpace);

    Label getTitle();

}
