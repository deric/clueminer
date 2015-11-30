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
