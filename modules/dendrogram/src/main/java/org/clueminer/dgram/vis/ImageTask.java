package org.clueminer.dgram.vis;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ImageTask<E extends Instance, C extends Cluster<E>> {

    private final Clustering<E, C> clustering;
    private final int width;
    private final int height;
    private final DendrogramVisualizationListener listener;
    private final DendrogramMapping mapping;

    public ImageTask(Clustering<E, C> clustering, int width, int height, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        this.clustering = clustering;
        this.width = width;
        this.height = height;
        this.listener = listener;
        this.mapping = mapping;
    }

    public Clustering<E, C> getClustering() {
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
