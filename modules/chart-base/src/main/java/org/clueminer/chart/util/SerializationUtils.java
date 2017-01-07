package org.clueminer.chart.util;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.Serializable;
import org.clueminer.chart.util.SerializableArea;
import org.clueminer.chart.util.SerializableBasicStroke;
import org.clueminer.chart.util.SerializablePoint2D;
import org.clueminer.chart.util.SerializableShape;
import org.clueminer.chart.util.SerializationWrapper;

/**
 * An abstract class containing utility functions for serialization.
 */
public abstract class SerializationUtils {

    /**
     * Default constructor that prevents creation of class.
     */
    private SerializationUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes sure an object is serializable, otherwise a serializable wrapper
     * will be returned.
     *
     * @param o Object to be serialized.
     * @return A serializable object, or a serializable wrapper.
     */
    public static Serializable wrap(Object o) {
        if (o == null || o instanceof Serializable) {
            return (Serializable) o;
        }

		// See Java bug 4305099:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4305099
        if (o instanceof BasicStroke) {
            BasicStroke stroke = (BasicStroke) o;
            return new SerializableBasicStroke(stroke);
        }

		// See Java bug 4263142 until Java 1.6:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4263142
        if ((o instanceof Point2D.Double) || (o instanceof Point2D.Float)) {
            Point2D point = (Point2D) o;
            return new SerializablePoint2D(point);
        }

        if (o instanceof Area) {
            Area area = (Area) o;
            return new SerializableArea(area);
        }

        if (o instanceof Shape) {
            Shape shape = (Shape) o;
            return new SerializableShape(shape);
        }

        throw new IllegalArgumentException(String.format(
                "Failed to make value of type %s serializable.",
                o.getClass().getName()
        ));
    }

    /**
     * Makes sure a regular object is returned, wrappers for serialization will
     * be removed.
     *
     * @param o Deserialized object.
     * @return A regular (unwrapped) object.
     */
    public static Object unwrap(Serializable o) {
        if (o instanceof SerializationWrapper<?>) {
            SerializationWrapper<?> wrapper = (SerializationWrapper<?>) o;
            return wrapper.unwrap();
        }
        return o;
    }
}
