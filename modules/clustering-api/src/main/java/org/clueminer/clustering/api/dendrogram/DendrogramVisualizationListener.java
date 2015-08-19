package org.clueminer.clustering.api.dendrogram;

import java.awt.Image;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface DendrogramVisualizationListener<E extends Instance, C extends Cluster<E>> {

    void clusteringFinished(Clustering<E, C> clustering);

    /**
     * Triggered when new dendrogram preview was rendered
     *
     * @param preview
     */
    void previewUpdated(Image preview);
}
