package org.clueminer.explorer;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.struct.ClusterList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringChildFactory extends ChildFactory<ClusteringNode> {

    @Override
    protected boolean createKeys(List<ClusteringNode> toPopulate) {
        ClusteringNode[] objs = new ClusteringNode[5];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new ClusteringNode(new ClusterList(5));
        }
        toPopulate.addAll(Arrays.asList(objs));
        return true;
    }

    @Override
    protected Node createNodeForKey(ClusteringNode key) {
        Node result = new AbstractNode(Children.create(new ClusteringChildFactory(), true), Lookups.singleton(key));
        result.setDisplayName(key.toString());
        return result;
    }

}
