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

import java.awt.Dimension;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Exportable;

/**
 * Interface for a dendrogram displayer
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface DendroViewer<E extends Instance, C extends Cluster<E>> extends Exportable {

    /**
     * One heatmap size should be same as dendrogram branches spacing
     *
     * @return size of one heatmap cell
     */
    Dimension getElementSize();

    void addDendrogramDataListener(DendrogramDataListener listener);

    void removeDendrogramDataListener(DendrogramDataListener listener);

    void addClusteringListener(ClusteringListener<E, C> listener);

    /**
     * Fire an event when user modifies clustering (changing cutoff)
     *
     * @param clust
     */
    void fireClusteringChanged(Clustering<E, C> clust);

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

    /**
     * True when evaluation bar is visible
     *
     * @return
     */
    boolean isEvaluationVisible();

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
     * @param update     trigger update immediately?
     */
    void setClustering(Clustering clustering, boolean update);

    /**
     * Trigger re-rendering data
     */
    void update();

    /**
     * Set clustering to display
     *
     * @param dendroMapping
     * @param update        whether trigger update immediately
     */
    void setDendrogramMapping(DendrogramMapping dendroMapping, boolean update);

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
