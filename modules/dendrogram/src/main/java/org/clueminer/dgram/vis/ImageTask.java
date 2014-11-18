package org.clueminer.dgram.vis;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;

/**
 *
 * @author Tomas Barton
 */
public class ImageTask {

    private final Clustering<? extends Cluster> clustering;
    private final int width;
    private final int height;
    private final DendrogramVisualizationListener listener;
    private final DendrogramMapping mapping;

    public ImageTask(Clustering<? extends Cluster> clustering, int width, int height, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        this.clustering = clustering;
        this.width = width;
        this.height = height;
        this.listener = listener;
        this.mapping = mapping;
    }

    public Clustering<? extends Cluster> getClustering() {
        return clustering;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public DendrogramVisualizationListener getListener() {
        return listener;
    }

    public DendrogramMapping getMapping() {
        return mapping;
    }

}
