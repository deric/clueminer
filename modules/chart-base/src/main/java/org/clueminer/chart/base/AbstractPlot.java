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
import org.clueminer.chart.factory.ThemeFactory;
import org.clueminer.chart.theme.Theme;
import org.clueminer.chart.util.GraphicsUtils;
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

    public AbstractPlot(int width, int height) {
        theme = ThemeFactory.getInstance().getDefault();
        setBounds(0, 0, width, height);
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

}
