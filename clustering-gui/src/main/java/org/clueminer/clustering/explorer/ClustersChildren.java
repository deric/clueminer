package org.clueminer.clustering.explorer;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ClustersChildren extends Children.Keys<Cluster> {

    private Clustering<Cluster> clusters;

    public ClustersChildren(Clustering<Cluster> clusters) {
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