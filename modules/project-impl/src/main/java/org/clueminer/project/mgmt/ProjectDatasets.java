package org.clueminer.project.mgmt;

import java.util.Collection;
import org.clueminer.dataset.api.Dataset;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ProjectDatasets extends Children.Keys<Dataset> {

    public ProjectDatasets(Collection<? extends Dataset> datasets) {
        setKeys(datasets);
    }

    @Override
    protected Node[] createNodes(Dataset key) {
        return new Node[]{new DatasetNode(key)};
    }
}
