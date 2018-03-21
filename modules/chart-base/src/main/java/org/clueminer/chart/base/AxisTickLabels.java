/*
 * Copyright (C) 2011-2018 clueminer.org
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
import java.util.HashMap;
import java.util.Map;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class AxisTickLabels extends AbstractDrawable implements Drawable {

    /** parent */
    private final AxisTick axisTick;

    private Theme theme;

    /**
     * Constructor
     *
     * @param axisTick
     */
    protected AxisTickLabels(AxisTick axisTick) {
        this.axisTick = axisTick;
        this.theme = axisTick.getAxis().getPlot().getTheme();
    }

    @Override
    public void draw(DrawingContext context) {
        Graphics2D g = context.getGraphics();

        g.setFont(theme.getAxisTickLabelsFont());

        g.setColor(theme.getAxisTickLabelsColor());

        if (axisTick.getAxis().getOrientation() == Orientation.VERTICAL && theme.isYAxisTicksVisible()) { // Y-Axis

            double xWidth = axisTick.getAxis().getTitle().getBounds().getWidth();
            double xOffset = axisTick.getAxis().getTitle().getBounds().getX() + xWidth;
            double yOffset = axisTick.getAxis().getPaintZone().getY();
            double height = axisTick.getAxis().getPaintZone().getHeight();
            double maxTickLabelWidth = 0;
            Map<Double, TextLayout> axisLabelTextLayouts = new HashMap<>();

            for (int i = 0; i < axisTick.getTickLabels().size(); i++) {

                String tickLabel = axisTick.getTickLabels().get(i);
                // System.out.println("** " + tickLabel);
                double tickLocation = axisTick.getTickLocations().get(i);
                double flippedTickLocation = yOffset + height - tickLocation;

                if (tickLabel != null && flippedTickLocation > yOffset && flippedTickLocation < yOffset + height) { // some are null for logarithmic axes
                    FontRenderContext frc = g.getFontRenderContext();
                    TextLayout axisLabelTextLayout = new TextLayout(tickLabel, theme.getAxisTickLabelsFont(), frc);
                    Rectangle2D tickLabelBounds = axisLabelTextLayout.getBounds();
                    double boundWidth = tickLabelBounds.getWidth();
                    if (boundWidth > maxTickLabelWidth) {
                        maxTickLabelWidth = boundWidth;
                    }
                    axisLabelTextLayouts.put(tickLocation, axisLabelTextLayout);
                }
            }

            for (Double tickLocation : axisLabelTextLayouts.keySet()) {

                TextLayout axisLabelTextLayout = axisLabelTextLayouts.get(tickLocation);
                Shape shape = axisLabelTextLayout.getOutline(null);
                Rectangle2D tickLabelBounds = shape.getBounds();

                double flippedTickLocation = yOffset + height - tickLocation;

                AffineTransform orig = g.getTransform();
                AffineTransform at = new AffineTransform();
                double boundWidth = tickLabelBounds.getWidth();
                double xPos;
                switch (theme.getYAxisLabelAlignment()) {
                    case RIGHT:
                        xPos = xOffset + maxTickLabelWidth - boundWidth;
                        break;
                    case CENTER:
                        xPos = xOffset + (maxTickLabelWidth - boundWidth) / 2;
                        break;
                    case LEFT:
                    default:
                        xPos = xOffset;
                }
                at.translate(xPos, flippedTickLocation + tickLabelBounds.getHeight() / 2.0);
                g.transform(at);
                g.fill(shape);
                g.setTransform(orig);

            }

            // bounds
            bounds = new Rectangle2D.Double(xOffset, yOffset, maxTickLabelWidth, height);
            // g.setColor(Color.blue);
            // g.draw(bounds);

        } // X-Axis
        else if (axisTick.getAxis().getOrientation() == Orientation.HORIZONTAL && theme.isXAxisTicksVisible()) {

            double xOffset = axisTick.getAxis().getPaintZone().getX();
            double yOffset = axisTick.getAxis().getTitle().getBounds().getY();
            double width = axisTick.getAxis().getPaintZone().getWidth();
            double maxTickLabelHeight = 0;

            // System.out.println("axisTick.getTickLabels().size(): " + axisTick.getTickLabels().size());
            for (int i = 0; i < axisTick.getTickLabels().size(); i++) {

                String tickLabel = axisTick.getTickLabels().get(i);
                // System.out.println("tickLabel: " + tickLabel);
                double tickLocation = axisTick.getTickLocations().get(i);
                double shiftedTickLocation = xOffset + tickLocation;

                if (tickLabel != null && shiftedTickLocation > xOffset && shiftedTickLocation < xOffset + width) { // some are null for logarithmic axes

                    FontRenderContext frc = g.getFontRenderContext();
                    TextLayout textLayout = new TextLayout(tickLabel, theme.getAxisTickLabelsFont(), frc);
                    // System.out.println(textLayout.getOutline(null).getBounds().toString());

                    // Shape shape = v.getOutline();
                    AffineTransform rot = AffineTransform.getRotateInstance(-1 * Math.toRadians(theme.getXAxisLabelRotation()), 0, 0);
                    Shape shape = textLayout.getOutline(rot);
                    Rectangle2D tickLabelBounds = shape.getBounds2D();

                    AffineTransform orig = g.getTransform();
                    AffineTransform at = new AffineTransform();
                    double xPos;
                    switch (theme.getXAxisLabelAlignment()) {
                        case LEFT:
                            xPos = shiftedTickLocation;
                            break;
                        case RIGHT:
                            xPos = shiftedTickLocation - tickLabelBounds.getWidth();
                            break;
                        case CENTER:
                        default:
                            xPos = shiftedTickLocation - tickLabelBounds.getWidth() / 2.0;
                    }
                    // System.out.println("tickLabelBounds: " + tickLabelBounds.toString());
                    double shiftX = -1 * tickLabelBounds.getX() * Math.sin(Math.toRadians(theme.getXAxisLabelRotation()));
                    double shiftY = -1 * (tickLabelBounds.getY() + tickLabelBounds.getHeight());
                    // System.out.println(shiftX);
                    // System.out.println("shiftY: " + shiftY);
                    at.translate(xPos + shiftX, yOffset + shiftY);

                    g.transform(at);
                    g.fill(shape);
                    g.setTransform(orig);

                    // // debug box
                    // g.setColor(Color.MAGENTA);
                    // g.draw(new Rectangle2D.Double(xPos, yOffset - tickLabelBounds.getHeight(), tickLabelBounds.getWidth(), tickLabelBounds.getHeight()));
                    // g.setColor(getChartPainter().getStyleManager().getAxisTickLabelsColor());
                    if (tickLabelBounds.getHeight() > maxTickLabelHeight) {
                        maxTickLabelHeight = tickLabelBounds.getHeight();
                    }
                }
            }

            // bounds
            bounds = new Rectangle2D.Double(xOffset, yOffset - maxTickLabelHeight, width, maxTickLabelHeight);
            // g.setColor(Color.blue);
            // g.draw(bounds);

        }

    }

}
