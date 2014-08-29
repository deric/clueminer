package org.clueminer.project.api;

/**
 *
 * @author Tomas Barton
 */
public interface WorkspaceProvider {

    public Workspace getCurrentWorkspace();

    public boolean hasCurrentWorkspace();

    public Workspace[] getWorkspaces();
}