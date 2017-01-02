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
import java.awt.Stroke;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.Drawable;
import org.clueminer.chart.api.DrawingContext;
import org.clueminer.chart.factory.ThemeFactory;
import org.clueminer.chart.renderer.LinearRenderer2D;
import org.clueminer.chart.theme.Theme;
import org.clueminer.chart.util.GraphicsUtils;
import org.clueminer.chart.util.Orientation;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 */
public class AbstractPlot extends DrawableContainer implements Drawable {

    private static final long serialVersionUID = -6475620400218498764L;

    private Theme theme;
    protected Dataset<? extends Instance> dataset;

    public AbstractPlot() {
        theme = ThemeFactory.getInstance().getDefault();
    }

    public void setTheme(Theme t) {
        this.theme = t;
    }

    public Theme getTheme() {
        return theme;
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

    public void setDataset(Dataset<? extends Instance> data) {
        this.dataset = data;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    protected Axis createAxis(boolean isLogscale, Orientation orient) {
        Axis ax;
        if (isLogscale) {
            throw new UnsupportedOperationException("not supported yet");
        } else {
            ax = new BaseAxis(new LinearRenderer2D(), orient);
        }
        add(ax);
        return ax;
    }

}
