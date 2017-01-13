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

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public class GraphicsEnv {

    /**
     * In headless mode (server without screen) we can not get default screen
     * device unless users connects with 'ssh -X ...'
     *
     * Headless mode does not allow creating top level components (JFrame) but
     * other graphics like panels, images (JPanel) etc. should be fine.
     *
     * (That means that we environment variable DISPLAY is set)
     *
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage compatibleImage(int width, int height) {
        BufferedImage image;

        if (GraphicsEnvironment.isHeadless()) {
            //without graphical display we can't have java auto-detection
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            image = ge.getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        }

        return image;
    }

}
