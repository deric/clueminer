package org.clueminer.clustering.explorer;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ClusterMembers extends Children.Keys<Instance> {
    
    public ClusterMembers(Cluster cluster){
        setKeys(cluster);
    }

    @Override
    protected Node[] createNodes(Instance key) {
        return new Node[]{ new InstanceNode(key)};
    }
}
