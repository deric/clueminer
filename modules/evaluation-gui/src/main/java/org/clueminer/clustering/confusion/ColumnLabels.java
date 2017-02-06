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
package org.clueminer.clustering.confusion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

/**
 *
 * @author Tomas Barton
 */
public class ColumnLabels extends AbstractLabels {

    private static final long serialVersionUID = 3616092058872720426L;

    @Override
    protected void render(Graphics2D g) {
        //we draw strings in rows and then we rotate the whole image
        String str;
        int coordX;
        if (hasData()) {
            g.setColor(Color.black);

            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getHeight();
            int width;
            // clockwise 90 degrees
            g.rotate(Math.PI / 2.0);
            maxWidth = 0;
            for (int col = 0; col < labels.length; col++) {
                coordX = (col + 1) * elementSize.width - elementSize.width / 2 - height / 2;
                str = labels[col];
                width = (int) (g.getFont().getStringBounds(str, frc).getWidth());
                checkMax(width);
                g.drawString(str, 0, -coordX);
            }
            g.rotate(-Math.PI / 2.0);
            if (changedMax) {
                changedMax = false;
                recalculate();
            }
        }
    }

    /**
     * We care only about width
     *
     * @param size
     */
    @Override
    protected void updateSize(Dimension size) {
        if (elementSize.width != size.width) {
            elementSize.width = size.width;
            resetCache();
        }
    }

    @Override
    public boolean hasData() {
        return (labels != null && labels.length > 0);
    }

    @Override
    protected void recalculate() {
        int width = 50;
        int height = 30 + maxWidth;
        if (hasData()) {
            width = elementSize.width * labels.length + 1;
        }
        this.size.width = width;
        this.size.height = height;
        double fsize = elementSize.width * 0.1;
        defaultFont = defaultFont.deriveFont((float) fsize);
        setMinimumSize(this.size);
        setSize(this.size);
        setPreferredSize(size);
    }

}
