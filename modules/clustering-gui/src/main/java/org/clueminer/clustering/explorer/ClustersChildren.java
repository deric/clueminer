package org.clueminer.clustering.explorer;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClustersChildren<E extends Instance, C extends Cluster<E>> extends Children.Keys<Cluster> {

    private Clustering<E, C> clusters;

    public ClustersChildren(Clustering<E, C> clusters) {
        this.clusters = clusters;

        setKeys(clusters);
    }

    @Override
    protected Node[] createNodes(Cluster cluster) {
        return new Node[]{new ClusterNode(cluster)};
    }

    @Override
    protected void addNotify() {
    }
}
