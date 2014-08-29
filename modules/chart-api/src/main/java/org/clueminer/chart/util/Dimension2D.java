package org.clueminer.chart.util;

import java.io.Serializable;
import java.util.Locale;

/**
 * <p>
 * Class that stores the horizontal and vertical extent of an object.</p>
 * <p>
 * This implementation adds support of double values to
 * {@code java.awt.geom.Dimension2D}.</p>
 */
public abstract class Dimension2D extends java.awt.geom.Dimension2D
        implements Serializable {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 6961198271520384282L;

    /**
     * Creates a new Dimension2D object.
     */
    public Dimension2D() {
    }

    /**
     * Class that stores double values.
     */
    public static class Double extends Dimension2D {

        /**
         * Version id for serialization.
         */
        private static final long serialVersionUID = -4341712269787906650L;

        /**
         * Horizontal extension.
         */
        private double width;
        /**
         * Vertical extension.
         */
        private double height;

        /**
         * Creates a new Dimension2D object with zero width and height.
         */
        public Double() {
            setSize(0.0, 0.0);
        }

        /**
         * Creates a new Dimension2D object with the specified width and
         * height.
         *
         * @param width  Width.
         * @param height Height.
         */
        public Double(double width, double height) {
            setSize(width, height);
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format(Locale.US,
                                 "%s[width=%f, height=%f]", //$NON-NLS-1$
                                 getClass().getName(), width, height);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof java.awt.geom.Dimension2D)) {
                return false;
            }
            java.awt.geom.Dimension2D dim = (java.awt.geom.Dimension2D) obj;
            return (getWidth() == dim.getWidth())
                    && (getHeight() == dim.getHeight());
        }

        @Override
        public int hashCode() {
            long bits = java.lang.Double.doubleToLongBits(getWidth());
            bits ^= java.lang.Double.doubleToLongBits(getHeight()) * 31;
            return ((int) bits) ^ ((int) (bits >> 32));
        }
    }
}
