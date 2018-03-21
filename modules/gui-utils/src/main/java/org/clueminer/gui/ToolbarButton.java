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

import java.awt.Insets;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Tomas Barton
 */
public class ToolbarButton extends JButton {

    private static final long serialVersionUID = -6983942848067934704L;

    public static ToolbarButton getButton(Action action) {
        return new ToolbarButton(action);
    }

    public ToolbarButton(Action action) {
        super(action);

        setVerticalAlignment(SwingConstants.TOP);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);

        setMargin(new Insets(6, 6, 6, 6));
        setBorderPainted(false);
    }

    public void toggleLabel(boolean show) {
        if (show) {
            showText();
        } else {
            hideText();
        }
    }

    public void hideText() {
        setText("");
    }

    public void showText() {
        setText((String) getAction().getValue(Action.NAME));
    }

    public void toggleIcon(boolean small) {
        if (small) {
            showSmallIcon();
        } else {
            showBigIcon();
        }
    }

    public void showSmallIcon() {
        setIcon((ImageIcon) getAction().getValue(Action.SMALL_ICON));
    }

    public void showBigIcon() {
        setIcon((ImageIcon) getAction().getValue(Action.LARGE_ICON_KEY));
    }
}