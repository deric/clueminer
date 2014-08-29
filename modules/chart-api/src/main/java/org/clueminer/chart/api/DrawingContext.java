package org.clueminer.chart.api;

import java.awt.Graphics2D;

/**
 * Class that stores an object for drawing and additional context information
 * that may be necessary to determine how to draw the object. This includes
 * information on drawing quality and the target media (screen, paper, etc.).
 */
public class DrawingContext {

    /**
     * Data type that describes the quality mode of drawing operations.
     */
    public static enum Quality {

        /**
         * Fast drawing mode.
         */
        DRAFT,
        /**
         * Standard drawing mode.
         */
        NORMAL,
        /**
         * High quality drawing mode.
         */
        QUALITY
    }

    /**
     * Data type that describes the type of the drawing target.
     */
    public static enum Target {

        /**
         * Bitmap drawing target consisting of pixels.
         */
        BITMAP,
        /**
         * Vector drawing target consisting of lines and curves.
         */
        VECTOR
    }

    /**
     * Graphics instance used for drawing.
     */
    private final Graphics2D graphics;
    /**
     * Quality level used for drawing.
     */
    private final Quality quality;
    /**
     * Target media.
     */
    private final Target target;

    /**
     * Initializes a new context with a {@code Graphics2D} object.
     *
     * @param graphics Object for drawing geometry.
     */
    public DrawingContext(Graphics2D graphics) {
        this(graphics, Quality.NORMAL, Target.BITMAP);
    }

    /**
     * Initializes a new context with a {@code Graphics2D} object.
     *
     * @param graphics Object for drawing geometry.
     * @param quality  Drawing quality.
     * @param target   Target media.
     */
    public DrawingContext(Graphics2D graphics, Quality quality, Target target) {
        this.graphics = graphics;
        this.quality = quality;
        this.target = target;
    }

    /**
     * Returns the object for drawing geometry.
     *
     * @return Graphics object.
     */
    public Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * Returns the desired display quality.
     *
     * @return Display quality mode.
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Returns the drawing target.
     *
     * @return Drawing target.
     */
    public Target getTarget() {
        return target;
    }
}
