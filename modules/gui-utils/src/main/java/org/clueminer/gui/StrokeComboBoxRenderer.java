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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author viorel.gheba
 */
public class StrokeComboBoxRenderer extends JComponent implements ListCellRenderer {
    private static final long serialVersionUID = 6537280174087128952L;

    private Stroke stroke;

    public StrokeComboBoxRenderer(int width, int height) {
        setOpaque(true);
        setPreferredSize(new Dimension(width, height));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        stroke = (Stroke) value;
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Stroke oldStroke = g2.getStroke();

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        g2.setStroke(stroke);
        g2.setColor(getForeground());

        int x = 5;
        while (getWidth() <= 2 * x) {
            x--;
        }

        g2.drawLine(x, getHeight() / 2, getWidth() - x, getHeight() / 2);

        g2.setStroke(oldStroke);
    }

}
