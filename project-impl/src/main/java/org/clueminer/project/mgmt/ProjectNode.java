package org.clueminer.project.mgmt;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.project.api.Project;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ProjectNode extends AbstractNode {

    private Project project;

    public ProjectNode(Project project) {
        // TODO get all datasets from project
        super(new ProjectDatasets(project.getLookup().lookupAll(Dataset.class)), Lookups.singleton(project));
        this.project = project;
        setShortDescription("<html><b>" + project.getName() + "</html>");
    }

}
