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

}
