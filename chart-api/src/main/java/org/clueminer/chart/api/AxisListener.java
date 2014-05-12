package org.clueminer.chart.api;

/**
 * Interface that provides a function to listen for changes in axes.
 */
public interface AxisListener {

    /**
     * Notified if the range of the axis has changed.
     *
     * @param axis Axis instance that has changed.
     * @param min  New minimum value.
     * @param max  New maximum value.
     */
    void rangeChanged(Axis axis, Number min, Number max);
}
