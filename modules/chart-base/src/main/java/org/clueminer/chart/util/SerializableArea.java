package org.clueminer.chart.util;

import java.awt.geom.Area;
import org.clueminer.chart.util.SerializableShape;
import org.clueminer.chart.util.SerializationWrapper;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.geom.Area}.
 */
public class SerializableArea implements SerializationWrapper<Area> {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -2861579645195882742L;

    /**
     * Serialized instance.
     */
    private final SerializableShape shape;

    /**
     * Initializes a new wrapper with an {@code Area} instance.
     *
     * @param area Wrapped object.
     */
    public SerializableArea(Area area) {
        shape = new SerializableShape(area);
    }

    /**
     * Creates a new instance of the wrapped class using the data from the
     * wrapper. This is used for deserialization.
     *
     * @return An instance containing the data from the wrapper.
     */
    public Area unwrap() {
        return new Area(shape.unwrap());
    }
}
