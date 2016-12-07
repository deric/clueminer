/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.project.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectInformation;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceInformation;
import org.clueminer.project.api.WorkspaceListener;
import org.clueminer.project.io.LoadTask;
import org.clueminer.project.io.SaveTask;
import org.clueminer.spi.WorkspaceDuplicateProvider;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ProjectController.class)
public class ProjectControllerImpl implements ProjectController {

    private static final long serialVersionUID = 5431087169257499516L;

    private enum EventType {

        INITIALIZE, SELECT, UNSELECT, CLOSE, DISABLE, PROJECT_SELECTED
    };
    private List<Project> projects = new ArrayList<>();
    private Project currentProject;
    private final List<WorkspaceListener> listeners;
    private WorkspaceImpl temporaryOpeningWorkspace;
    private String appTitle;
    private static final Logger LOG = LoggerFactory.getLogger(ProjectControllerImpl.class);

    public ProjectControllerImpl() {

        //Listeners
        listeners = new ArrayList<>();
        listeners.addAll(Lookup.getDefault().lookupAll(WorkspaceListener.class));
    }

    @Override
    public void startup(final String appTitle) {
        final String OPEN_LAST_PROJECT_ON_STARTUP = "Open_Last_Project_On_Startup";
        final String NEW_PROJECT_ON_STARTUP = "New_Project_On_Startup";
        boolean openLastProject = NbPreferences.forModule(ProjectControllerImpl.class).getBoolean(OPEN_LAST_PROJECT_ON_STARTUP, false);
        boolean newProjectStartup = NbPreferences.forModule(ProjectControllerImpl.class).getBoolean(NEW_PROJECT_ON_STARTUP, false);
        this.appTitle = appTitle;

        //Default project
        if (!openLastProject && newProjectStartup) {
            newProject();
        }
    }

    protected void addProject(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    @Override
    public void setCurrentProject(Project project) {
        if (currentProject != project) {
            this.currentProject = project;
            fireWorkspaceEvent(EventType.PROJECT_SELECTED, null);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = appTitle + " - " + currentProject.getName();
                    frame.setTitle(title);
                }
            });
        }
    }

    @Override
    public void newProject() {
        //closeCurrentProject();
        ProjectImpl project = new ProjectImpl();
        addProject(project);
        openProject(project);
    }

    @Override
    public Runnable openProject(File file) {
        return new LoadTask(file);
    }

    @Override
    public Runnable saveProject(Project project) {
        if (project.getLookup().lookup(ProjectInformationImpl.class).hasFile()) {
            File file = project.getLookup().lookup(ProjectInformationImpl.class).getFile();
            return saveProject(project, file);
        }
        return null;
    }

    @Override
    public Runnable saveProject(Project project, File file) {
        project.getLookup().lookup(ProjectInformationImpl.class).setFile(file);
        SaveTask saveTask = new SaveTask(project, file);
        return saveTask;
    }

    /**
     * if another project exists it should be set as currentProject when the
     * project's tab becomes active
     */
    @Override
    public void closeCurrentProject() {
        if (hasCurrentProject()) {
            currentProject.getLookup().lookup(ProjectInformation.class).close();
            currentProject = null;
        }
    }

    @Override
    public void removeProject(Project project) {
        if (getCurrentProject() == project) {
            closeCurrentProject();
        }
        //removeProject(project);
    }

    @Override
    public List<Project> getProjects() {
        return projects;
    }

    @Override
    public void setProjects(List<Project> projects) {
        final String OPEN_LAST_PROJECT_ON_STARTUP = "Open_Last_Project_On_Startup";
        boolean openLastProject = NbPreferences.forModule(ProjectControllerImpl.class).getBoolean(OPEN_LAST_PROJECT_ON_STARTUP, false);

        Project lastOpenProject = null;
        for (Project p : projects) {
            if (p.getLookup().lookup(ProjectInformationImpl.class).hasFile()) {
                ProjectImpl pImpl = (ProjectImpl) p;
                pImpl.init();
                addProject(p);
                pImpl.getLookup().lookup(ProjectInformationImpl.class).close();
                if (p == getCurrentProject()) {
                    lastOpenProject = p;
                }
            }
        }

        if (openLastProject && lastOpenProject != null && !lastOpenProject.getLookup().lookup(ProjectInformationImpl.class).isInvalid() && lastOpenProject.getLookup().lookup(ProjectInformationImpl.class).hasFile()) {
            openProject(lastOpenProject);
        } else {
            //newProject();
        }
    }

    @Override
    public Workspace newWorkspace(Project project) {
        Workspace workspace = project.getLookup().lookup(WorkspaceProviderImpl.class).newWorkspace();

        //Event
        fireWorkspaceEvent(EventType.INITIALIZE, workspace);
        return workspace;
    }

    @Override
    public void deleteWorkspace(Workspace workspace) {
        WorkspaceInformation wi = workspace.getLookup().lookup(WorkspaceInformation.class);
        WorkspaceProviderImpl workspaceProvider = wi.getProject().getLookup().lookup(WorkspaceProviderImpl.class);

        Workspace toSelectWorkspace = null;
        if (getCurrentWorkspace() == workspace) {
            toSelectWorkspace = workspaceProvider.getPrecedingWorkspace(workspace);
        }

        workspaceProvider.removeWorkspace(workspace);

        //Event
        fireWorkspaceEvent(EventType.CLOSE, workspace);

        if (getCurrentWorkspace() == workspace) {
            //Select the one before, or after
            if (toSelectWorkspace == null) {
                closeCurrentProject();
            } else {
                openWorkspace(toSelectWorkspace);
            }
        }

    }

    public void openProject(Project project) {
        final ProjectImpl projectImpl = (ProjectImpl) project;
        final ProjectInformationImpl projectInformationImpl = projectImpl.getLookup().lookup(ProjectInformationImpl.class);
        final WorkspaceProviderImpl workspaceProviderImpl = project.getLookup().lookup(WorkspaceProviderImpl.class);

        addProject(project);
        setCurrentProject(projectImpl);
        projectInformationImpl.open();
        if (!workspaceProviderImpl.hasCurrentWorkspace()) {
            if (workspaceProviderImpl.getWorkspaces().length == 0) {
                Workspace workspace = newWorkspace(project);
                openWorkspace(workspace);
            } else {
                Workspace workspace = workspaceProviderImpl.getWorkspaces()[0];
                openWorkspace(workspace);
            }
        } else {
            fireWorkspaceEvent(EventType.SELECT, workspaceProviderImpl.getCurrentWorkspace());
        }
    }

    @Override
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Checks whether a project is opened in current workspace
     *
     * @return true when current project exists
     */
    @Override
    public boolean hasCurrentProject() {
        return currentProject != null;
    }

    @Override
    public WorkspaceImpl getCurrentWorkspace() {
        if (hasCurrentProject()) {
            temporaryOpeningWorkspace = null;
            return getCurrentProject().getLookup().lookup(WorkspaceProviderImpl.class).getCurrentWorkspace();
        } else if (temporaryOpeningWorkspace != null) {
            return temporaryOpeningWorkspace;
        }
        LOG.info("no current workspace");
        return null;
    }

    @Override
    public void closeCurrentWorkspace() {
        WorkspaceImpl workspace = getCurrentWorkspace();
        if (workspace != null) {
            workspace.getLookup().lookup(WorkspaceInformationImpl.class).close();

            //Event
            fireWorkspaceEvent(EventType.UNSELECT, workspace);
        }
    }

    @Override
    public void openWorkspace(Workspace workspace) {
        closeCurrentWorkspace();
        getCurrentProject().getLookup().lookup(WorkspaceProviderImpl.class).setCurrentWorkspace(workspace);
        workspace.getLookup().lookup(WorkspaceInformationImpl.class).open();
        LOG.info("opening workspace");
        //Event
        fireWorkspaceEvent(EventType.SELECT, workspace);
    }

    @Override
    public void cleanWorkspace(Workspace workspace) {
    }

    @Override
    public Workspace duplicateWorkspace(Workspace workspace) {
        if (hasCurrentProject()) {
            Workspace duplicate = newWorkspace(getCurrentProject());
            for (WorkspaceDuplicateProvider dp : Lookup.getDefault().lookupAll(WorkspaceDuplicateProvider.class)) {
                dp.duplicate(workspace, duplicate);
            }
            openWorkspace(duplicate);
            return duplicate;
        }
        return null;
    }

    @Override
    public void renameProject(Project project, final String name) {
        project.getLookup().lookup(ProjectInformationImpl.class).setName(name);
    }

    @Override
    public void renameWorkspace(Workspace workspace, String name) {
        workspace.getLookup().lookup(WorkspaceInformationImpl.class).setName(name);
    }

    @Override
    public void setSource(Workspace workspace, String source) {
        workspace.getLookup().lookup(WorkspaceInformationImpl.class).setSource(source);
    }

    /**
     * Hack to have a current workspace when opening workspace
     *
     * @param temporaryOpeningWorkspace the opening workspace or null
     */
    public void setTemporaryOpeningWorkspace(WorkspaceImpl temporaryOpeningWorkspace) {
        this.temporaryOpeningWorkspace = temporaryOpeningWorkspace;
        if (temporaryOpeningWorkspace != null) {
            //Init controllers with empty models
            fireWorkspaceEvent(EventType.INITIALIZE, temporaryOpeningWorkspace);
        }
    }

    @Override
    public void addWorkspaceListener(WorkspaceListener workspaceListener) {
        synchronized (listeners) {
            listeners.add(workspaceListener);
        }
    }

    @Override
    public void removeWorkspaceListener(WorkspaceListener workspaceListener) {
        synchronized (listeners) {
            listeners.remove(workspaceListener);
        }
    }

    private void fireWorkspaceEvent(EventType event, Workspace workspace) {
        WorkspaceListener[] listenersArray;
        synchronized (listeners) {
            listenersArray = listeners.toArray(new WorkspaceListener[0]);
        }
        for (WorkspaceListener wl : listenersArray) {
            switch (event) {
                case INITIALIZE:
                    wl.initialize(workspace);
                    break;
                case SELECT:
                    wl.select(workspace);
                    break;
                case UNSELECT:
                    wl.unselect(workspace);
                    break;
                case CLOSE:
                    wl.close(workspace);
                    break;
                case DISABLE:
                    wl.disable();
                    break;
                case PROJECT_SELECTED:
                    wl.projectActivated(currentProject);
                    break;
                default:
                    LOG.warn("unsupported event type {}", event);
            }
        }
    }
}
