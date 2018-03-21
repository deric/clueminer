/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.project.api;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectController extends Serializable {

    void startup(String appTitle);

    void newProject();

    Runnable openProject(File file);

    Runnable saveProject(Project project);

    Runnable saveProject(Project project, File file);

    /**
     * Currently active project at given workspace
     *
     * @return active project
     */
    Project getCurrentProject();

    /**
     * Checks whether there is some project in workspace
     *
     * @return
     */
    boolean hasCurrentProject();

    /**
     * Multiple project could be opened at the same time (in different tab)
     *
     * @param project
     */
    void setCurrentProject(Project project);

    void renameProject(Project project, String name);

    void closeCurrentProject();

    /**
     * All opened projects
     *
     * @return list of opened projects
     */
    List<Project> getProjects();

    /**
     * Should be used at initialization workspace (loading session from last
     * time)
     *
     * @param projects
     */
    void setProjects(List<Project> projects);

    /**
     * When project is closed, should be removed by this method
     *
     * @param project
     */
    void removeProject(Project project);

    Workspace newWorkspace(Project project);

    void deleteWorkspace(Workspace workspace);

    void renameWorkspace(Workspace workspace, String name);

    Workspace getCurrentWorkspace();

    void openWorkspace(Workspace workspace);

    void closeCurrentWorkspace();

    void cleanWorkspace(Workspace workspace);

    Workspace duplicateWorkspace(Workspace workspace);

    void setSource(Workspace workspace, String source);

    void addWorkspaceListener(WorkspaceListener workspaceListener);

    void removeWorkspaceListener(WorkspaceListener workspaceListener);
}
