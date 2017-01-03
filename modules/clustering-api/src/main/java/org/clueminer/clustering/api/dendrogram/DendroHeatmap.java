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
package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public interface DendroHeatmap {

    /**
     * Draws heatmap into an image
     *
     * @param size dimension of resulting image
     * @return
     */
    public BufferedImage drawData(Dimension size);

    /**
     * Data necessary for rendering a heatmap
     *
     * @param dendroData
     */
    public void setData(DendrogramMapping dendroData);

    /**
     * Should create new buffered image from scratch
     */
    public void resetCache();

    /**
     * Set heatmap color scheme
     *
     * @param scheme
     */
    public void setColorScheme(ColorScheme scheme);

    /**
     *
     * @return color scheme of the heatmap
     */
    public ColorScheme getScheme();

}
