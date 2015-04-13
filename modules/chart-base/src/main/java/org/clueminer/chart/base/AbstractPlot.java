package org.clueminer.chart.base;

/*
 * Copyright (C) 2011-2015 clueminer.org
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


import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.theme.BaseTheme;
import org.clueminer.chart.theme.Theme;
import org.clueminer.chart.util.GraphicsUtils;

/**
 *
 * @author deric
 */
public class AbstractPlot extends DrawableContainer implements Drawable {

    private Theme theme;

    public AbstractPlot() {
        this.theme = new BaseTheme();
    }

    @Override
    public void draw(DrawingContext context) {
        Graphics2D graphics = context.getGraphics();

        Paint bg = theme.getChart().getBackground();
        if (bg != null) {
            GraphicsUtils.fillPaintedShape(graphics, getBounds(), bg, null);
        }

        Stroke stroke = theme.getBorderStroke();
        if (stroke != null) {
            Paint fg = theme.getBorderColor();
            GraphicsUtils.drawPaintedShape(
                    graphics, getBounds(), fg, null, stroke);
        }

        drawComponents(context);
    }

}
