package org.clueminer.clustering.api.dendrogram;

import java.awt.Image;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public interface DendrogramVisualizationListener {

    void clusteringFinished(Clustering<? extends Cluster> clustering);

    /**
     * Triggered when new dendrogram preview was rendered
     *
     * @param preview
     */
    void previewUpdated(Image preview);
}
