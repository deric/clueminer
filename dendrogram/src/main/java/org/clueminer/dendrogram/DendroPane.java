package org.clueminer.dendrogram;

import java.awt.Color;
import java.awt.Dimension;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dendrogram.gui.ColorScheme;

/**
 *
 * @author Tomas Barton
 */
public interface DendroPane {

    public DendrogramData getDendrogramData();

    public boolean useDoubleGradient();
    
    public boolean isAntiAliasing();

    /**
     * Size of single square in heatmap
     *
     * @return dimension of element in dendrogram
     */
    public Dimension getElementSize();

    /**
     *
     * @return panel background
     */
    public Color getBackground();

    /**
     *
     * @return color scheme used in heatmap
     */
    public ColorScheme getScheme();

    /**
     * E.g. when user changes cutoff
     *
     * @param clust
     */
    public void fireClusteringChanged(Clustering clust);
    
    
    public String formatNumber(Object number);
}
