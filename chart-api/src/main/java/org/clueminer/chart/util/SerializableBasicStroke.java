package org.clueminer.chart.util;

import java.awt.BasicStroke;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.BasicStroke}.
 */
public class SerializableBasicStroke
        implements SerializationWrapper<BasicStroke> {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -9087891720495398485L;

    /**
     * Line width.
     */
    private final float width;
    /**
     * End cap.
     */
    private final int cap;
    /**
     * Line join mode.
     */
    private final int join;
    /**
     * Miter limit.
     */
    private final float miterlimit;
    /**
     * Dash array.
     */
    private final float[] dash;
    /**
     * Dash phase.
     */
    private final float dash_phase;

    /**
     * Initializes a new wrapper with a {@code BasicStroke} instance.
     *
     * @param stroke Wrapped object.
     */
    public SerializableBasicStroke(BasicStroke stroke) {
        width = stroke.getLineWidth();
        cap = stroke.getEndCap();
        join = stroke.getLineJoin();
        miterlimit = stroke.getMiterLimit();
        dash = stroke.getDashArray();
        dash_phase = stroke.getDashPhase();

    }

    /**
     * Creates a new stroke instance of the wrapped class using the data from
     * the wrapper. This is used for deserialization.
     *
     * @return A stroke instance containing the data from the wrapper.
     */
    @Override
    public BasicStroke unwrap() {
        return new BasicStroke(width, cap, join, miterlimit, dash, dash_phase);
    }
}
