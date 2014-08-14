package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.utils.Exportable;

/**
 * Interface for a dendrogram displayer
 *
 * @author Tomas Barton
 */
public interface DendroViewer extends Exportable {

    /**
     * One heatmap size should be same as dendrogram branches spacing
     *
     * @return size of one heatmap cell
     */
    public Dimension getElementSize();

    public void addDendrogramDataListener(DendrogramDataListener listener);

    public void removeDendrogramDataListener(DendrogramDataListener listener);

    public void addClusteringListener(ClusteringListener listener);

    /**
     * Fire an event when user modifies clustering (changing cutoff)
     *
     * @param clust
     */
    public void fireClusteringChanged(Clustering clust);

    public boolean isHorizontalTreeVisible();

    public boolean isVerticalTreeVisible();

    public boolean isLegendVisible();

    public void setHorizontalTreeVisible(boolean show);

    public void setVerticalTreeVisible(boolean show);

    public void setLegendVisible(boolean show);

    public void setLabelsVisible(boolean show);

    /**
     * Show Silhoulette evaluation
     *
     * @param show
     */
    public void setEvaluationVisible(boolean show);

    public boolean isLabelVisible();

    /**
     * Set width of heatmap cell in pixels.
     *
     * @param width
     * @param isAdjusting true when value is continously changing
     * @param source
     */
    public void setCellWidth(int width, boolean isAdjusting, Object source);

    /**
     * When true dendrogram size will be auto-computed according to window size
     *
     * @param fitToPanel
     */
    public void setFitToPanel(boolean fitToPanel);

    /**
     * Set height of heatmap cell in pixels.
     *
     * @param height
     * @param isAdjusting
     * @param source
     */
    public void setCellHeight(int height, boolean isAdjusting, Object source);

    public boolean isFitToPanel();

    public void setDataset(DendrogramMapping dataset);

    /**
     *
     * @param clustering
     */
    public void setClustering(Clustering clustering);

}
