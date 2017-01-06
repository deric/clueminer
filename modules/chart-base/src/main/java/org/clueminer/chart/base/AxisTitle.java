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
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Label;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Orientation;

/**
 * Render axis title
 *
 * @author deric
 */
public class AxisTitle extends AbstractDrawable implements Drawable, Label {

    /** parent */
    private final Axis axis;

    /** the title text */
    private String text = ""; // default to ""

    /**
     * Constructor
     *
     * @param axis the axis
     */
    protected AxisTitle(Axis axis) {
        this.axis = axis;
    }

    @Override
    public void draw(DrawingContext context) {
        Graphics2D g = context.getGraphics();

        Theme theme = axis.getPlot().getTheme();

        g.setColor(theme.getChartFontColor());
        g.setFont(theme.getAxisTitleFont());

        if (axis.getOrientation() == Orientation.VERTICAL) {

            if (text != null && !text.trim().equalsIgnoreCase("") && theme.isYAxisTitleVisible()) {

                FontRenderContext frc = g.getFontRenderContext();
                TextLayout nonRotatedTextLayout = new TextLayout(text, theme.getAxisTitleFont(), frc);
                Rectangle2D nonRotatedRectangle = nonRotatedTextLayout.getBounds();

                // ///////////////////////////////////////////////
                int xOffset = (int) (axis.getPaintZone().getX() + nonRotatedRectangle.getHeight());
                int yOffset = (int) ((axis.getPaintZone().getHeight() + nonRotatedRectangle.getWidth()) / 2.0 + axis.getPaintZone().getY());

                AffineTransform rot = AffineTransform.getRotateInstance(-1 * Math.PI / 2, 0, 0);
                Shape shape = nonRotatedTextLayout.getOutline(rot);

                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();

                at.translate(xOffset, yOffset);
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);

                // ///////////////////////////////////////////////
                // System.out.println(nonRotatedRectangle.getHeight());
                // bounds
                bounds = new Rectangle2D.Double(xOffset - nonRotatedRectangle.getHeight(), yOffset - nonRotatedRectangle.getWidth(), nonRotatedRectangle.getHeight() + theme.getAxisTitlePadding(), nonRotatedRectangle.getWidth());
                // g.setColor(Color.blue);
                // g.draw(bounds);
            } else {
                bounds = new Rectangle2D.Double(axis.getPaintZone().getX(), axis.getPaintZone().getY(), 0, axis.getPaintZone().getHeight());
            }

        } else {

            if (text != null && !text.trim().equalsIgnoreCase("") && theme.isXAxisTitleVisible()) {

                FontRenderContext frc = g.getFontRenderContext();
                TextLayout textLayout = new TextLayout(text, theme.getAxisTitleFont(), frc);
                Rectangle2D rectangle = textLayout.getBounds();
                // System.out.println(rectangle);

                double xOffset = axis.getPaintZone().getX() + (axis.getPaintZone().getWidth() - rectangle.getWidth()) / 2.0;
                double yOffset = axis.getPaintZone().getY() + axis.getPaintZone().getHeight() - rectangle.getHeight();

                // textLayout.draw(g, (float) xOffset, (float) (yOffset - rectangle.getY()));
                Shape shape = textLayout.getOutline(null);
                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                at.translate((float) xOffset, (float) (yOffset - rectangle.getY()));
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);

                bounds = new Rectangle2D.Double(xOffset,
                        yOffset - theme.getAxisTitlePadding(),
                        rectangle.getWidth(),
                        rectangle.getHeight() + theme.getAxisTitlePadding());
                // g.setColor(Color.blue);
                // g.draw(bounds);

            } else {
                bounds = new Rectangle2D.Double(axis.getPaintZone().getX(), axis.getPaintZone().getY() + axis.getPaintZone().getHeight(), axis.getPaintZone().getWidth(), 0);
                // g.setColor(Color.blue);
                // g.draw(bounds);

            }
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {

        this.text = text;
    }

    @Override
    public int getSizeHint() {
        return 15;
    }
}
