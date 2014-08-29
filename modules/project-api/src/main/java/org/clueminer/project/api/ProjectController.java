package org.clueminer.project.api;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectController extends Serializable {

    public void startup();

    public void newProject();

    public Runnable openProject(File file);

    public Runnable saveProject(Project project);

    public Runnable saveProject(Project project, File file);

    /**
     * Currently active project at given workspace
     *
     * @return active project
     */
    public Project getCurrentProject();

    /**
     * Checks whether there is some project in workspace
     *
     * @return
     */
    public boolean hasCurrentProject();

    /**
     * Multiple project could be opened at the same time (in different tab)
     *
     * @param project
     * @return
     */
    public void setCurrentProject(Project project);

    public void renameProject(Project project, String name);

    public void closeCurrentProject();

    /**
     * All opened projects
     *
     * @return list of opened projects
     */
    public List<Project> getProjects();

    /**
     * Should be used at initialization workspace (loading session from last
     * time)
     *
     * @param projects
     */
    public void setProjects(List<Project> projects);

    /**
     * When project is closed, should be removed by this method
     *
     * @param project
     */
    public void removeProject(Project project);

    public Workspace newWorkspace(Project project);

    public void deleteWorkspace(Workspace workspace);

    public void renameWorkspace(Workspace workspace, String name);

    public Workspace getCurrentWorkspace();

    public void openWorkspace(Workspace workspace);

    public void closeCurrentWorkspace();

    public void cleanWorkspace(Workspace workspace);

    public Workspace duplicateWorkspace(Workspace workspace);

    public void setSource(Workspace workspace, String source);

    public void addWorkspaceListener(WorkspaceListener workspaceListener);

    public void removeWorkspaceListener(WorkspaceListener workspaceListener);
}
