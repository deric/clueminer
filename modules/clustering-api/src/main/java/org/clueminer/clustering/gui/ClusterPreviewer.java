package org.clueminer.clustering.gui;

import java.awt.Graphics;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 * General interface for cluster visualization which could be looked up and then
 * exported.
 *
 * Previewer should be a paint-able graphic component (e.g. JPanel)
 *
 * @author Tomas Barton
 */
public interface ClusterPreviewer {

    public void setClustering(Clustering<Cluster> clustering);

    /**
     * ClusterPreviewer should inherit from JComponent
     *
     * @param g
     */
    public void paint(Graphics g);
}
