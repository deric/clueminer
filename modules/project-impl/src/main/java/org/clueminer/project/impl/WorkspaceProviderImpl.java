package org.clueminer.project.impl;

import java.util.ArrayList;
import java.util.List;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceProvider;

/**
 *
 * @author Tomas Barton
 */
public class WorkspaceProviderImpl implements WorkspaceProvider {
 private transient WorkspaceImpl currentWorkspace;
    private transient Project project;
    private transient List<Workspace> workspaces;

    public WorkspaceProviderImpl(Project project) {
        init(project);
    }

    private void init(Project project) {
        this.project = project;
        workspaces = new ArrayList<Workspace>();
    }

    public WorkspaceImpl newWorkspace() {
        WorkspaceImpl workspace = new WorkspaceImpl(project);
        workspaces.add(workspace);
        return workspace;
    }

    public void addWorkspace(Workspace workspace) {
        workspaces.add(workspace);
    }

    public void removeWorkspace(Workspace workspace) {
        workspaces.remove(workspace);
    }

    public Workspace getPrecedingWorkspace(Workspace workspace) {
        Workspace[] ws = getWorkspaces();
        int index = -1;
        for (int i = 0; i < ws.length; i++) {
            if (ws[i] == workspace) {
                index = i;
            }
        }
        if (index != -1 && index >= 1) {
            //Get preceding
            return ws[index - 1];
        } else if (index == 0 && ws.length > 1) {
            //Get following
            return ws[1];
        }
        return null;
    }

    @Override
    public WorkspaceImpl getCurrentWorkspace() {
        return currentWorkspace;
    }

    @Override
    public Workspace[] getWorkspaces() {
        return workspaces.toArray(new Workspace[0]);
    }

    public void setCurrentWorkspace(Workspace currentWorkspace) {
        this.currentWorkspace = (WorkspaceImpl) currentWorkspace;
    }

    @Override
    public boolean hasCurrentWorkspace() {
        return currentWorkspace != null;
    }
}
