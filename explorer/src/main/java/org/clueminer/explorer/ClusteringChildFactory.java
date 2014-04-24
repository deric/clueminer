package org.clueminer.explorer;

import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringChildFactory extends ChildFactory<ClusteringNode> {

    @Override
    protected boolean createKeys(List<ClusteringNode> toPopulate) {
        return true;
    }

}
