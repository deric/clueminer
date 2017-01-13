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
package org.clueminer.gui;

import java.awt.Dimension;
import java.awt.Stroke;
import javax.swing.JComboBox;

/**
 *
 * @author Tomas Barton
 */
public class StrokeComboBox extends JComboBox {
    private static final long serialVersionUID = -6259450920839549519L;

    public StrokeComboBox() { this(StrokeGenerator.getStrokes(), 100, 30); }

    public StrokeComboBox(Stroke[] strokes, int width, int height) {
        super(strokes);
        setRenderer(new StrokeComboBoxRenderer(width, height));
        Dimension prefSize = getPreferredSize();
        prefSize.height = height + getInsets().top + getInsets().bottom;
        setPreferredSize(prefSize);
        setMaximumRowCount(10);
    }

}
