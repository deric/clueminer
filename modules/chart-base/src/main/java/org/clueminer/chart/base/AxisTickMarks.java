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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.api.Theme;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public class AxisTickMarks extends AbstractDrawable implements Drawable {

    /** parent */
    private AxisTick axisTick;

    /** the bounds */
    private Rectangle2D bounds = new Rectangle2D.Double();

    /**
     * Constructor
     *
     * @param axisTick
     */
    protected AxisTickMarks(AxisTick axisTick) {
        this.axisTick = axisTick;
    }

    @Override
    public void draw(DrawingContext context) {
        Graphics2D g = context.getGraphics();
        Theme theme = axisTick.getAxis().getPlot().getTheme();

        g.setColor(theme.getAxisTickMarksColor());
        g.setStroke(theme.getAxisTickMarksStroke());

        if (axisTick.getAxis().getOrientation() == Orientation.VERTICAL && theme.isYAxisTicksVisible()) { // Y-Axis

            double xOffset = axisTick.getAxisTickLabels().getBounds().getX() + axisTick.getAxisTickLabels().getBounds().getWidth() + theme.getAxisTickPadding();
            double yOffset = axisTick.getAxis().getPaintZone().getY();

            // bounds
            bounds = new Rectangle2D.Double(xOffset, yOffset, theme.getAxisTickMarkLength(), axisTick.getAxis().getPaintZone().getHeight());
            // g.setColor(Color.yellow);
            // g.draw(bounds);

            // tick marks
            if (theme.isAxisTicksMarksVisible()) {

                for (int i = 0; i < axisTick.getTickLabels().size(); i++) {

                    double tickLocation = axisTick.getTickLocations().get(i);
                    double flippedTickLocation = yOffset + axisTick.getAxis().getPaintZone().getHeight() - tickLocation;
                    if (flippedTickLocation > bounds.getY() && flippedTickLocation < bounds.getY() + bounds.getHeight()) {

                        Shape line = new Line2D.Double(xOffset, flippedTickLocation, xOffset + theme.getAxisTickMarkLength(), flippedTickLocation);
                        g.draw(line);
                    }
                }
            }

            // Line
            if (theme.isAxisTicksLineVisible()) {

                Shape line
                        = new Line2D.Double(xOffset + theme.getAxisTickMarkLength(), yOffset, xOffset + theme.getAxisTickMarkLength(), yOffset
                                + axisTick.getAxis().getPaintZone().getHeight());
                g.draw(line);

            }

        } // X-Axis
        else if (axisTick.getAxis().getOrientation() == Orientation.VERTICAL && theme.isXAxisTicksVisible()) {

            double xOffset = axisTick.getAxis().getPaintZone().getX();
            double yOffset = axisTick.getAxisTickLabels().getBounds().getY() - theme.getAxisTickPadding();

            // bounds
            bounds = new Rectangle2D.Double(xOffset,
                    yOffset - theme.getAxisTickMarkLength(),
                    axisTick.getAxis().getPaintZone().getWidth(),
                    theme.getAxisTickMarkLength());
            // g.setColor(Color.yellow);
            // g.draw(bounds);

            // tick marks
            if (theme.isAxisTicksMarksVisible()) {

                for (int i = 0; i < axisTick.getTickLabels().size(); i++) {

                    double tickLocation = axisTick.getTickLocations().get(i);
                    double shiftedTickLocation = xOffset + tickLocation;

                    if (shiftedTickLocation > bounds.getX() && shiftedTickLocation < bounds.getX() + bounds.getWidth()) {

                        Shape line = new Line2D.Double(shiftedTickLocation,
                                yOffset, xOffset + tickLocation, yOffset - theme.getAxisTickMarkLength());
                        g.draw(line);
                    }
                }
            }

            // Line
            if (theme.isAxisTicksLineVisible()) {

                g.setStroke(theme.getAxisTickMarksStroke());
                g.drawLine((int) xOffset, (int) (yOffset - theme.getAxisTickMarkLength()), (int) (xOffset + axisTick.getAxis().getPaintZone().getWidth()),
                        (int) (yOffset - theme.getAxisTickMarkLength()));
            }

        }
    }

}
