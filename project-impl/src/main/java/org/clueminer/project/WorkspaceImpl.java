package org.clueminer.project;

import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public class WorkspaceImpl implements Workspace {

    private transient InstanceContent instanceContent;
    private transient Lookup lookup;

    public WorkspaceImpl(Project project) {
        init(project);
    }

    private void init(Project project) {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);

        //Init Default Content
        WorkspaceInformationImpl workspaceInformationImpl = new WorkspaceInformationImpl(project);
        add(workspaceInformationImpl);
    }

    @Override
    public void add(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void remove(Object instance) {
        instanceContent.remove(instance);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}