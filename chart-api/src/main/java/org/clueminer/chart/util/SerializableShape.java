package org.clueminer.chart.util;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import org.clueminer.chart.util.GeometryUtils.PathSegment;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.Shape} (e.g. {@link java.awt.geom.Path2D}).
 */
public class SerializableShape implements SerializationWrapper<Shape> {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -8849270838795846599L;

    /**
     * Shape segments.
     */
    private final List<PathSegment> segments;
    /**
     * Flag to determine whether the class was of type Path2D.Double or
     * Path2D.Float.
     */
    private final boolean isDouble;

    /**
     * Initializes a new wrapper with a {@code Shape} instance.
     *
     * @param shape Wrapped object.
     */
    public SerializableShape(Shape shape) {
        segments = GeometryUtils.getSegments(shape);
        isDouble = !(shape instanceof Path2D.Float);
    }

    /**
     * Creates a new instance of the wrapped class using the data from the
     * wrapper. This is used for deserialization.
     *
     * @return An instance containing the data from the wrapper.
     */
    @Override
    public Shape unwrap() {
        return GeometryUtils.getShape(segments, isDouble);
    }
}
