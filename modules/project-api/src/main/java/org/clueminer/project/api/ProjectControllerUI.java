package org.clueminer.project.api;

import java.io.File;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectControllerUI {

    public void saveProject();

    public void saveAsProject();

    public void openProject(File file);

    public void renameProject(final String name);

    public void projectProperties();

    public void openFile();

    public Project newProject();

    public void closeProject();

    public boolean canNewProject();

    public boolean canCloseProject();

    public boolean canOpenFile();

    public boolean canSave();

    public boolean canSaveAs();

    public boolean canProjectProperties();
    
    public boolean isFileSupported(File file);
}
