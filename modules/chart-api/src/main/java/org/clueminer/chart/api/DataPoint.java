package org.clueminer.chart.api;

import java.awt.Shape;
import org.clueminer.chart.points.PointData;
import org.clueminer.chart.util.PointND;

/**
 * Class for storing points of a plot.
 */
public class DataPoint {

    /**
     * Axes and data values that were used to create the data point.
     */
    public final PointData data;
    /**
     * Position of the data point (n-dimensional).
     */
    public final PointND<Double> position;
    /**
     * Drawable that will be used to render the data point.
     */
    public final Drawable drawable;
    /**
     * Shape describing the data point.
     */
    public final Shape shape;
    /**
     * Drawable that will be used to render the value label.
     */
    public final Drawable labelDrawable;

    /**
     * Creates a new {@code DataPoint} object with the specified position,
     * {@code Drawable}, and shape.
     *
     * @param data          Data that this point was created from.
     * @param position      Coordinates in view/screen units.
     * @param drawable      Visual representation.
     * @param shape         Geometric shape of the point.
     * @param labelDrawable Visual representation of the value label.
     */
    public DataPoint(PointData data, PointND<Double> position,
            Drawable drawable, Shape shape, Drawable labelDrawable) {
        this.data = data;
        this.position = position;
        this.drawable = drawable;
        this.shape = shape;
        this.labelDrawable = labelDrawable;
    }
}
