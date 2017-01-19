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
package org.clueminer.visualize;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Controls component resizing using mouse wheel + CTRL key
 *
 * @author deric
 */
public class MouseWheelDriver implements MouseWheelListener {

    private final ClusterSetView clusterSet;

    public MouseWheelDriver(ClusterSetView clusterSet) {
        this.clusterSet = clusterSet;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        int height, width;
        Dimension dim = clusterSet.getChartDimension();
        if (dim == null) {
            return;
        }
        if (e.isControlDown() && !e.isAltDown()) {
            if (notches < 0) {
                height = (int) (dim.height * 0.9);
                if (height == dim.height) {
                    height -= 1;
                }
            } else {
                height = (int) (dim.height * 1.1);
                if (height == dim.height) {
                    height += 1;
                }
            }
            dim.height = height;
            clusterSet.setChartDimension(dim);
        }

    }

}
