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
package org.clueminer.clustering.api.dendrogram;

import java.awt.Color;
import java.awt.Dimension;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Distribution;

/**
 *
 * @author Tomas Barton
 */
public interface DendroPane {

    DendrogramMapping getDendrogramData();

    boolean useDoubleGradient();

    boolean isAntiAliasing();

    /**
     * Size of single square in heatmap
     *
     * @return dimension of element in dendrogram
     */
    Dimension getElementSize();

    /**
     *
     * @return panel background
     */
    Color getBackground();

    /**
     *
     * @return color scheme used in heatmap
     */
    ColorScheme getScheme();

    /**
     * E.g. when user changes cutoff
     *
     * @param clust
     */
    void fireClusteringChanged(Clustering clust);

    String formatNumber(Object number);

    /**
     * Heatmap is a visualization of data
     *
     * @return heatmap
     */
    DendroHeatmap getHeatmap();

    /**
     * Cutoff slider diameter
     *
     * @param sliderDiam
     */
    void setSliderDiameter(int sliderDiam);

    /**
     * Width of cut off slider
     *
     * @return
     */
    int getSliderDiameter();

    /**
     * Distribution of dataset
     *
     * @return
     */
    Distribution getDistribution();
}
