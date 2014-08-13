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

    public void setCellWidth(int width, boolean isAdjusting);

    public void setFitToPanel(boolean fitToPanel);

    public void setCellHeight(int height, boolean isAdjusting);

    public boolean isFitToPanel();

    public void setDataset(DendrogramMapping dataset);

    /**
     *
     * @param clustering
     */
    public void setClustering(Clustering clustering);

}
