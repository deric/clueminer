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
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.api.Tick;
import org.clueminer.chart.api.TickType;
import org.clueminer.chart.util.GraphicsUtils;

/**
 *
 * @author deric
 */
public class Grid extends AbstractDrawable implements Drawable {

    private static final long serialVersionUID = 5623352118160212157L;
    /**
     * Decides whether the horizontal grid lines at major ticks are drawn.
     */
    private boolean majorGridX = true;
    /**
     * Decides whether the vertical grid lines at major ticks are drawn.
     */
    private boolean majorGridY = true;
    /**
     * Paint to fill the grid lines at major ticks.
     */
    private Paint majorGridColor;

    /**
     * Decides whether the horizontal grid lines at minor ticks are drawn.
     */
    private boolean minorGridX;
    /**
     * Decides whether the vertical grid lines at minor ticks are drawn.
     */
    private boolean minorGridY;
    /**
     * Paint to fill the grid lines at minor ticks.
     */
    private Paint minorGridColor;

    private Plot plot;

    public Grid(Plot parent) {
        this.plot = parent;
    }

    @Override
    public void draw(DrawingContext context) {

        Graphics2D g = context.getGraphics();

        AffineTransform txOrig = g.getTransform();
        g.translate(getX(), getY());
        AffineTransform txOffset = g.getTransform();
        Rectangle2D bounds = getBounds();

        // Draw gridX
        if (isMajorGridX() || isMinorGridX()) {
            AxisRenderer axisXRenderer = plot.getAxisRenderer(AxisPosition.X);
            Axis axisX = plot.getAxis(AxisPosition.X);
            if (axisXRenderer != null && axisX != null && axisX.isValid()) {
                Shape shapeX = axisXRenderer.getShape();
                Rectangle2D shapeBoundsX = shapeX.getBounds2D();
                List<Tick> ticksX = axisXRenderer.getTicks(axisX);
                Line2D gridLineVert = new Line2D.Double(
                        -shapeBoundsX.getMinX(),
                        -shapeBoundsX.getMinY(),
                        -shapeBoundsX.getMinX(),
                        bounds.getHeight() - shapeBoundsX.getMinY()
                );
                for (Tick tick : ticksX) {
                    if ((tick.type == TickType.MAJOR && !isMajorGridX())
                            || (tick.type == TickType.MINOR && !isMinorGridX())) {
                        continue;
                    }
                    Point2D tickPoint = tick.position.getPoint2D();
                    if (tickPoint == null) {
                        continue;
                    }

                    Paint paint = majorGridColor;
                    if (tick.type == TickType.MINOR) {
                        paint = getMinorGridColor();
                    }
                    g.translate(tickPoint.getX(), tickPoint.getY());
                    GraphicsUtils.drawPaintedShape(
                            g, gridLineVert, paint, null, null);
                    g.setTransform(txOffset);
                }
            }
        }

        // Draw gridY
        if (isMajorGridY() || isMinorGridY()) {
            Axis axisY = plot.getAxis(AxisPosition.Y);
            AxisRenderer axisYRenderer = plot.getAxisRenderer(AxisPosition.Y);
            if (axisY != null && axisY.isValid() && axisYRenderer != null) {
                Shape shapeY = axisYRenderer.getShape();
                Rectangle2D shapeBoundsY = shapeY.getBounds2D();
                List<Tick> ticksY = axisYRenderer.getTicks(axisY);
                Line2D gridLineHoriz = new Line2D.Double(
                        -shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
                        bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
                );
                for (Tick tick : ticksY) {
                    boolean isMajorTick = tick.type == TickType.MAJOR;
                    boolean isMinorTick = tick.type == TickType.MINOR;
                    if ((isMajorTick && !isMajorGridY())
                            || (isMinorTick && !isMinorGridY())) {
                        continue;
                    }
                    Point2D tickPoint = tick.position.getPoint2D();
                    if (tickPoint == null) {
                        continue;
                    }

                    Paint paint = majorGridColor;
                    if (isMinorTick) {
                        paint = getMinorGridColor();
                    }
                    g.translate(tickPoint.getX(), tickPoint.getY());
                    GraphicsUtils.drawPaintedShape(
                            g, gridLineHoriz, paint, null, null);
                    g.setTransform(txOffset);
                }
            }
        }

        g.setTransform(txOrig);
    }

    public boolean isMajorGridX() {
        return majorGridX;
    }

    public void setMajorGridX(boolean majorGridX) {
        this.majorGridX = majorGridX;
    }

    public boolean isMajorGridY() {
        return majorGridY;
    }

    public void setMajorGridY(boolean majorGridY) {
        this.majorGridY = majorGridY;
    }

    public Paint getMajorGridColor() {
        return majorGridColor;
    }

    public void setMajorGridColor(Paint majorGridColor) {
        this.majorGridColor = majorGridColor;
    }

    public boolean isMinorGridX() {
        return minorGridX;
    }

    public void setMinorGridX(boolean minorGridX) {
        this.minorGridX = minorGridX;
    }

    public boolean isMinorGridY() {
        return minorGridY;
    }

    public void setMinorGridY(boolean minorGridY) {
        this.minorGridY = minorGridY;
    }

    public Paint getMinorGridColor() {
        return minorGridColor;
    }

    public void setMinorGridColor(Paint minorGridColor) {
        this.minorGridColor = minorGridColor;
    }

}
