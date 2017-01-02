/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.chart.base;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisListener;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Tick;
import org.clueminer.chart.api.TickType;
import org.clueminer.chart.graphics.Label;
import org.clueminer.chart.util.Dim;
import org.clueminer.chart.util.GeometryUtils;
import org.clueminer.chart.util.GraphicsUtils;
import org.clueminer.chart.util.MathUtils;
import org.clueminer.chart.util.Orientation;
import org.clueminer.chart.util.PointND;

/**
 * <p>
 * Class that represents an arbitrary axis.</p>
 * <p>
 * Functionality includes:</p>
 * <ul>
 * <li>Different ways of setting and getting the range of this axis</li>
 * <li>Administration of {@link AxisListener AxisListeners}</li>
 * </ul>
 */
public class InsideAxis extends AbstractAxis implements Axis, Serializable {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 5355772833362614591L;

    /**
     * Minimal value on axis.
     */
    private Number min;
    /**
     * Maximal value on axis.
     */
    private Number max;

    /**
     * Initializes a new instance with a specified automatic scaling mode, but
     * without minimum and maximum values.
     *
     * @param autoscaled {@code true} to turn automatic scaling on
     */
    private InsideAxis(boolean autoscaled) {
        axisListeners = new LinkedList<>();
        this.autoscaled = autoscaled;
    }

    /**
     * Initializes a new instance without minimum and maximum values.
     */
    public InsideAxis() {
        this(true);
    }

    /**
     * Initializes a new instance with the specified minimum and maximum values.
     *
     * @param min minimum value
     * @param max maximum value
     */
    public InsideAxis(Number min, Number max) {
        this(false);
        this.min = min;
        this.max = max;
    }

    public InsideAxis(AxisRenderer renderer, Orientation orient) {
        this(true);
        this.renderer = renderer;
        this.orientation = orient;
        this.min = 0;
        this.max = 1;
    }

    /**
     * Returns the minimum value to be displayed.
     *
     * @return Minimum value.
     */
    @Override
    public Number getMin() {
        return min;
    }

    /**
     * Sets the minimum value to be displayed.
     *
     * @param min Minimum value.
     */
    @Override
    public void setMin(Number min) {
        setRange(min, getMax());
    }

    /**
     * Returns the maximum value to be displayed.
     *
     * @return Maximum value.
     */
    @Override
    public Number getMax() {
        return max;
    }

    /**
     * Sets the maximum value to be displayed.
     *
     * @param max Maximum value.
     */
    @Override
    public void setMax(Number max) {
        setRange(getMin(), max);
    }

    /**
     * Returns the range of values to be displayed.
     *
     * @return Distance between maximum and minimum value.
     */
    @Override
    public double getRange() {
        return getMax().doubleValue() - getMin().doubleValue();
    }

    /**
     * Sets the range of values to be displayed.
     *
     * @param min Minimum value.
     * @param max Maximum value.
     */
    @Override
    public void setRange(Number min, Number max) {
        if ((getMin() != null) && getMin().equals(min)
                && (getMax() != null) && getMax().equals(max)) {
            return;
        }
        this.min = min;
        this.max = max;
        fireRangeChanged(min, max);
    }

    /**
     * Returns whether the currently set minimum and maximum values are valid.
     *
     * @return {@code true} when minimum and maximum values are correct,
     *         otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return MathUtils.isCalculatable(min) && MathUtils.isCalculatable(max);
    }

    @Override
    public void draw(DrawingContext context) {
        /* if (shapeLines == null || shapeLines.length == 0) {            return;
         }*/

        Graphics2D graphics = context.getGraphics();

        // Remember old state of Graphics2D instance
        AffineTransform txOrig = graphics.getTransform();
        graphics.translate(getX(), getY());
        Stroke strokeOld = graphics.getStroke();
        Paint paintOld = graphics.getPaint();

        // Draw axis shape
        Paint axisPaint = renderer.getShapeColor();
        Stroke axisStroke = renderer.getShapeStroke();
        boolean isShapeVisible = renderer.isShapeVisible();
        if (isShapeVisible) {
            Shape shape = renderer.getShape();
            GraphicsUtils.drawPaintedShape(
                    graphics, shape, axisPaint, null, axisStroke);
        }

        double fontSize
                = renderer.getTickFont().getSize2D();

        // Draw ticks
        boolean drawTicksMajor = renderer.isTicksVisible();
        boolean drawTicksMinor = renderer.isMinorTicksVisible();
        if (drawTicksMajor || (drawTicksMajor && drawTicksMinor)) {
            // Calculate tick positions (in pixel coordinates)
            List<Tick> ticks = renderer.getTicks(this);

            boolean isTickLabelVisible
                    = renderer.isTickLabelsVisible();
            boolean isTickLabelOutside = renderer.isTickLabelsOutside();
            double tickLabelRotation = renderer.getTickLabelRotation();
            double tickLabelDist = renderer.getTickLabelDistanceAbsolute();
            Line2D tickShape = new Line2D.Double();

            for (Tick tick : ticks) {
                // Draw tick
                if ((tick.position == null)
                        || (tick.normal == null)) {
                    continue;
                }
                Point2D tickPoint = tick.position.getPoint2D();
                Point2D tickNormal = tick.normal.getPoint2D();

                double tickLength;
                double tickAlignment;
                Paint tickPaint;
                Stroke tickStroke;
                if (TickType.MINOR.equals(tick.type)) {
                    tickLength = renderer.getTickMinorLengthAbsolute();
                    tickAlignment = renderer.getMinorTickAlignment();
                    tickPaint = renderer.getMinorTickColor();
                    tickStroke = renderer.getMinorTickStroke();
                } else {
                    tickLength = renderer.getTickLengthAbsolute();
                    tickAlignment = renderer.getTickAlignment();
                    tickPaint
                            = renderer.getTickColor();
                    tickStroke = renderer.getTickStroke();
                }

                double tickLengthInner = tickLength * tickAlignment;
                double tickLengthOuter = tickLength * (1.0 - tickAlignment);

                if ((drawTicksMajor && (tick.type == TickType.MAJOR)
                        || tick.type == TickType.CUSTOM) || (drawTicksMinor
                        && tick.type == TickType.MINOR)) {
                    tickShape.setLine(
                            tickPoint.getX() - tickNormal.getX() * tickLengthInner,
                            tickPoint.getY() - tickNormal.getY() * tickLengthInner,
                            tickPoint.getX() + tickNormal.getX() * tickLengthOuter,
                            tickPoint.getY() + tickNormal.getY() * tickLengthOuter
                    );
                    GraphicsUtils.drawPaintedShape(
                            graphics, tickShape, tickPaint, null, tickStroke);
                }

                // Draw label
                if (isTickLabelVisible && (tick.type == TickType.MAJOR
                        || tick.type == TickType.CUSTOM)) {
                    String tickLabelText = tick.label;
                    if (tickLabelText != null && !tickLabelText.trim().isEmpty()) {
                        Label tickLabel = new Label(tickLabelText);
                        tickLabel.setFont(renderer.getTickFont());
                        // TODO Allow separate colors for ticks and tick labels?
                        tickLabel.setColor(tickPaint);
                        double labelDist = tickLengthOuter + tickLabelDist;
                        layoutLabel(tickLabel, tickPoint, tickNormal,
                                labelDist, isTickLabelOutside, tickLabelRotation);
                        tickLabel.draw(context);
                    }
                }
            }
        }

        // Draw axis label
        if (renderer.getLabel() != null) {
            Label axisLabel = new Label(renderer.getLabel());
            double tickLength = renderer.getTickLengthAbsolute();
            double tickAlignment = renderer.getTickAlignment();
            double tickLengthOuter = tickLength * (1.0 - tickAlignment);
            double tickLabelDistance = renderer.getTickLabelDistanceAbsolute();

            double labelDistance = renderer.getLabelDistance() * fontSize;
            double labelDist
                    = tickLengthOuter + tickLabelDistance + fontSize + labelDistance;
            double axisLabelPos
                    = (getMin().doubleValue() + getMax().doubleValue()) * 0.5;
            boolean isTickLabelOutside = renderer.isTickLabelsOutside();

            PointND<Double> labelPos = renderer.getPosition(this, axisLabelPos, false, true);
            PointND<Double> labelNormal = renderer.getNormal(this, axisLabelPos, false, true);

            if (labelPos != null && labelNormal != null) {
                layoutLabel(axisLabel, labelPos.getPoint2D(),
                        labelNormal.getPoint2D(), labelDist,
                        isTickLabelOutside, axisLabel.getRotation());
                axisLabel.draw(context);
            }
        }

        graphics.setPaint(paintOld);
        graphics.setStroke(strokeOld);
        graphics.setTransform(txOrig);
    }

    private void layoutLabel(Label label, Point2D labelPos, Point2D labelNormal,
            double labelDist, boolean isLabelOutside, double rotation) {
        Rectangle2D labelSize = label.getTextRectangle();
        Shape marginShape = new Rectangle2D.Double(
                0, 0,
                labelSize.getWidth() + 2.0 * labelDist, labelSize.getHeight() + 2.0 * labelDist
        );
        Rectangle2D marginBounds = marginShape.getBounds2D();
        label.setRotation(rotation);
        if ((rotation % 360.0) != 0.0) {
            marginShape = AffineTransform.getRotateInstance(
                    Math.toRadians(-rotation),
                    marginBounds.getCenterX(),
                    marginBounds.getCenterY()
            ).createTransformedShape(marginShape);
        }
        marginBounds = marginShape.getBounds2D();

        double intersRayLength = marginBounds.getHeight() * marginBounds.getHeight()
                + marginBounds.getWidth() * marginBounds.getWidth();
        double intersRayDir = (isLabelOutside ? -1.0 : 1.0) * intersRayLength;
        List<Point2D> descriptionBoundsIntersections = GeometryUtils.intersection(
                marginBounds,
                new Line2D.Double(
                        marginBounds.getCenterX(),
                        marginBounds.getCenterY(),
                        marginBounds.getCenterX() + intersRayDir * labelNormal.getX(),
                        marginBounds.getCenterY() + intersRayDir * labelNormal.getY()
                )
        );
        if (!descriptionBoundsIntersections.isEmpty()) {
            Point2D inters = descriptionBoundsIntersections.get(0);
            double intersX = inters.getX() - marginBounds.getCenterX();
            double intersY = inters.getY() - marginBounds.getCenterY();
            double posX = labelPos.getX() - intersX - labelSize.getWidth() / 2.0;
            double posY = labelPos.getY() - intersY - labelSize.getHeight() / 2.0;

            label.setBounds(posX, posY, labelSize.getWidth(), labelSize.getHeight());
        }
    }

    @Override
    public Dimension2D getPreferredSize() {
        double fontSize = renderer.getTickFont().getSize2D();
        double tickLength = renderer.getTickLengthAbsolute();
        double tickAlignment = renderer.getTickAlignment();
        double tickLengthOuter = tickLength * (1.0 - tickAlignment);
        double labelDistance = renderer.getTickLabelDistanceAbsolute() + tickLengthOuter;
        double minSize = fontSize + labelDistance + tickLengthOuter;
        return new Dim.Double(minSize, minSize);
    }

}
