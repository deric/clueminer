package org.clueminer.project.mgmt;

import java.util.Collection;
import org.clueminer.project.api.Project;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ProjectsChildren extends Children.Keys<Project> {

    private final Collection<? extends Project> projects;

    public ProjectsChildren(Collection<? extends Project> projects) {
        this.projects = projects;

        setKeys(projects);
    }

    @Override
    protected Node[] createNodes(Project project) {
        return new Node[]{new ProjectNode(project)};
    }

    @Override
    protected void addNotify() {
    }
}
