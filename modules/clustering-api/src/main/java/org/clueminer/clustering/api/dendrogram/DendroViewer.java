package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
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
    Dimension getElementSize();

    void addDendrogramDataListener(DendrogramDataListener listener);

    void removeDendrogramDataListener(DendrogramDataListener listener);

    void addClusteringListener(ClusteringListener listener);

    /**
     * Fire an event when user modifies clustering (changing cutoff)
     *
     * @param clust
     */
    void fireClusteringChanged(Clustering clust);

    boolean isHorizontalTreeVisible();

    boolean isVerticalTreeVisible();

    boolean isLegendVisible();

    void setHorizontalTreeVisible(boolean show);

    void setVerticalTreeVisible(boolean show);

    void setLegendVisible(boolean show);

    void setLabelsVisible(boolean show);

    /**
     * Show Silhoulette evaluation
     *
     * @param show
     */
    void setEvaluationVisible(boolean show);

    boolean isLabelVisible();

    /**
     * Set width of heatmap cell in pixels.
     *
     * @param width
     * @param isAdjusting true when value is continously changing
     * @param source
     */
    void setCellWidth(int width, boolean isAdjusting, Object source);

    /**
     * When true dendrogram size will be auto-computed according to window size
     *
     * @param fitToPanel
     */
    void setFitToPanel(boolean fitToPanel);

    /**
     * Set height of heatmap cell in pixels.
     *
     * @param height
     * @param isAdjusting
     * @param source
     */
    void setCellHeight(int height, boolean isAdjusting, Object source);

    boolean isFitToPanel();

    void setDataset(DendrogramMapping dataset);

    DendrogramMapping getDendrogramMapping();

    /**
     *
     * @param clustering
     */
    void setClustering(Clustering clustering);

    /**
     * Fired after optimization of dendrogram rows tree order
     *
     * @param source
     * @param rows
     */
    void fireRowMappingChanged(Object source, HierarchicalResult rows);

    /**
     * Fired after optimization of dendrogram columns tree order
     *
     * @param source
     * @param columns
     */
    void fireColumnsMappingChanged(Object source, HierarchicalResult columns);

}
