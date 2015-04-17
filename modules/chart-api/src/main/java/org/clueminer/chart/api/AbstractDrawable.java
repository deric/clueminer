package org.clueminer.chart.api;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.clueminer.chart.util.Dim;

/**
 * Abstract implementation of the {@link Drawable} interface.
 * This class implements common functionality like the different ways for
 * getting and setting the bounding rectangle of the drawable object.
 */
public abstract class AbstractDrawable implements Drawable, Serializable {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -684598008467326484L;

    /**
     * Boundaries of the drawable object.
     */
    private final Rectangle2D bounds;

    private Dim preferredSize;

    /**
     * Creates an AbstractDrawable.
     */
    public AbstractDrawable() {
        bounds = new Rectangle2D.Double();
    }

    /**
     * Returns the bounds of this {@code Drawable}.
     *
     * @return a bounding rectangle
     */
    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    /**
     * Returns the x-position of the bounds.
     *
     * @return horizontal position of the upper-left corner of the bounding
     *         rectangle.
     */
    @Override
    public double getX() {
        return bounds.getX();
    }

    /**
     * Returns the y-position of the bounds.
     *
     * @return vertical position of the upper-left corner of the bounding
     *         rectangle.
     */
    @Override
    public double getY() {
        return bounds.getY();
    }

    /**
     * Returns the width of the bounds.
     *
     * @return horizontal extent.
     */
    @Override
    public double getWidth() {
        return bounds.getWidth();
    }

    /**
     * Returns the height of the bounds.
     *
     * @return vertical extent.
     */
    @Override
    public double getHeight() {
        return bounds.getHeight();
    }

    /**
     * Sets the bounds to the specified bounding rectangle.
     *
     * @param bounds rectangle containing the component.
     */
    @Override
    public void setBounds(Rectangle2D bounds) {
        setBounds(bounds.getX(), bounds.getY(),
                  bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Sets the bounds to the specified coordinates, width and height.
     * This method should be used when overriding functionality.
     *
     * @param x      horizontal position of the upper-left corner
     * @param y      vertical position of the upper-left corner
     * @param width  horizontal extent
     * @param height vertical extent
     */
    @Override
    public void setBounds(double x, double y, double width, double height) {
        System.out.println("setting bounds " + x + ", " + y + " w= " + width + ", h= " + height);
        bounds.setFrame(x, y, width, height);
    }

    /**
     * Returns the preferred size of the {@code Drawable}.
     *
     * @return horizontal and vertical extent that wants to be reached
     */
    @Override
    public Dimension2D getPreferredSize() {
        if (preferredSize == null) {
            preferredSize = new Dim.Double(bounds.getWidth(), bounds.getHeight());
        }
        return preferredSize;
    }

    @Override
    public void setPosition(double x, double y) {
        bounds.setFrame(x, y, bounds.getWidth(), bounds.getHeight());
    }
}
