package org.clueminer.chart.api;

import java.awt.Shape;
import org.clueminer.chart.util.PointND;

/**
 * Class for storing the tick mark of an axis.
 */
public class Tick extends DataPoint {

    /**
     * Type of tick mark.
     */
    public static enum TickType {

        /**
         * Major tick mark.
         */
        MAJOR,
        /**
         * Minor tick mark.
         */
        MINOR,
        /**
         * User-defined tick mark.
         */
        CUSTOM
    }

    /**
     * The type of tick mark (major/minor/custom).
     */
    public final TickType type;
    /**
     * The normal of the tick mark.
     */
    public final PointND<Double> normal;
    /**
     * Label text associated with this tick mark.
     */
    public final String label;

    /**
     * Creates a new instance with the specified position, normal,
     * {@code Drawable}, point and label.
     *
     * @param type     Type of the tick mark.
     * @param position Coordinates.
     * @param normal   Normal.
     * @param drawable Representation.
     * @param point    Point.
     * @param label    Description.
     */
    public Tick(TickType type, PointND<Double> position, PointND<Double> normal,
            Drawable drawable, Shape point, String label) {
        super(null, position, drawable, point, null);
        this.type = type;
        this.normal = normal;
        this.label = label;
    }
}
