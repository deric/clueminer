package org.clueminer.clustering.gui;

import java.awt.Graphics;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 * General interface for cluster visualization which could be looked up and then
 * exported.
 *
 * Previewer should be a paint-able graphic component (e.g. JPanel)
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusterPreviewer<E extends Instance, C extends Cluster<E>> {

    public void setClustering(Clustering<E, C> clustering);

    /**
     * ClusterPreviewer should inherit from JComponent
     *
     * @param g
     */
    public void paint(Graphics g);
}
