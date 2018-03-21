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
package org.clueminer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Tomas Barton
 */
public class BottomBorder extends AbstractBorder implements Serializable {

    private static final long serialVersionUID = -5365039889338113577L;
    protected Color color = new Color(0x898c95);
    protected int thickness = 1;
    protected int gap = 1;

    public BottomBorder() {
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color old = g.getColor();
        g.setColor(color);
        for (int i = 0; i < thickness; i++) {
            g.drawLine(x, y + height - i - 1, x + width, y + height - i - 1);
        }
        g.setColor(old);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, gap, 0);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = 0;
        insets.top = 0;
        insets.right = 0;
        insets.bottom = gap;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
