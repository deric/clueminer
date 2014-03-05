package org.clueminer.project.mgmt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import org.clueminer.project.api.Project;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ProjectsNode extends AbstractNode implements PropertyChangeListener {
    private Collection<? extends Project> projects;

    public ProjectsNode(Collection<? extends Project> projects) {
        super(new ProjectsChildren(projects), Lookups.singleton(projects));
        this.projects = projects;
        setIconBaseWithExtension("org/clueminer/resources/cluster.png");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<? extends Project> getProjects() {
        return projects;
    }

}
