/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.dgram;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollPane;
import org.clueminer.clustering.api.dendrogram.DendroViewer;

/**
 * Handles resizing of dendrogram with mouse wheel
 *
 * @author Tomas Barton
 */
public class MouseWheelDriver implements MouseWheelListener {

    private final DendroViewer parent;
    private final JScrollPane scrollPane;

    public MouseWheelDriver(DendroViewer parent, JScrollPane scrollPane) {
        this.parent = parent;
        this.scrollPane = scrollPane;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        Dimension dim = parent.getElementSize();
        int height, width;
        if (e.isControlDown() && !e.isAltDown()) {
            parent.setFitToPanel(false);
            if (notches < 0) {
                height = (int) (dim.height * 0.9);
                if (height == dim.height) {
                    height -= 1;
                }
                parent.setCellHeight(height, false, this);
            } else {
                height = (int) (dim.height * 1.1);
                if (height == dim.height) {
                    height += 1;
                }
                parent.setCellHeight(height, false, this);
            }
        } else if (e.isAltDown() && !e.isControlDown()) {
            parent.setFitToPanel(false);
            if (notches < 0) {
                width = (int) (dim.width * 0.9);
                if (width == dim.width) {
                    width -= 1;
                }
                parent.setCellWidth(width, false, this);
            } else {
                width = (int) (dim.width * 1.1);
                if (width == dim.width) {
                    width += 1;
                }
                parent.setCellWidth(width, false, this);
            }
        } else {
            //pass events to scrollpanel
            scrollPane.dispatchEvent(e);
        }
    }

}
