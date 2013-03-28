package org.clueminer.project.api;

/**
 *
 * @author Tomas Barton
 */
public interface WorkspaceListener {

    /**
     * Notify a workspace has been created.
     *
     * @param workspace the workspace that was created
     */
    public void initialize(Workspace workspace);

    /**
     * Notify a workspace has become the selected workspace.
     *
     * @param workspace the workspace that was made current workspace
     */
    public void select(Workspace workspace);

    /**
     * Notify another workspace will be selected. The
     * <code>select()</code> always follows.
     *
     * @param workspace the workspace that is currently the selected workspace
     */
    public void unselect(Workspace workspace);

    /**
     * Notify a workspace will be closed, all data must be destroyed.
     *
     * @param workspace the workspace that is to be closed
     */
    public void close(Workspace workspace);

    /**
     * Notify no more workspace is currently selected, the project is empty.
     */
    public void disable();

    /**
     * Notify workspace, that a project has become active
     *
     * @param project
     */
    public void projectActivated(Project project);
}