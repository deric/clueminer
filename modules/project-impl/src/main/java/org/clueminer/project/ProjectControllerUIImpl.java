package org.clueminer.project;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.types.FileType;
import org.clueminer.gui.DialogFileFilter;
import org.clueminer.importer.ImportControllerUI;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.LongTaskListener;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.project.api.MostRecentFiles;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectControllerUI;
import org.clueminer.project.api.ProjectInformation;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceProvider;
import org.clueminer.spi.ProjectPropertiesUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ProjectControllerUI.class)
public class ProjectControllerUIImpl implements ProjectControllerUI {
    //Actions

    private boolean openProject = true;
    private boolean newProject = true;
    private boolean openFile = true;
    private boolean saveProject = false;
    private boolean saveAsProject = false;
    private boolean projectProperties = false;
    private boolean closeProject = false;
    private boolean newWorkspace = false;
    private boolean deleteWorkspace = false;
    private boolean cleanWorkspace = false;
    private boolean duplicateWorkspace = false;
    protected static String PROJECT_EXTENSION = ".clmx";
    //Project
    private ProjectController controller;
    //Utilities
    private final LongTaskExecutor longTaskExecutor;

    public ProjectControllerUIImpl() {

        controller = Lookup.getDefault().lookup(ProjectController.class);

        //Project IO executor
        longTaskExecutor = new LongTaskExecutor(true, "Project IO");
        longTaskExecutor.setDefaultErrorHandler(new LongTaskErrorHandler() {
            @Override
            public void fatalError(Throwable t) {
                unlockProjectActions();
                String txt = NbBundle.getMessage(ProjectControllerUIImpl.class, "ProjectControllerUI.error.open");
                String message = txt + "\n\n" + t.getMessage();
                if (t.getCause() != null) {
                    message = txt + "\n\n" + t.getCause().getClass().getSimpleName() + " - " + t.getCause().getMessage();
                }
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(msg);
            }
        });
        longTaskExecutor.setLongTaskListener(new LongTaskListener() {
            @Override
            public void taskFinished(LongTask task) {
                unlockProjectActions();
            }
        });
    }

    private void saveProject(Project project, File file) {
        lockProjectActions();

        final Runnable saveTask = controller.saveProject(project, file);
        final String fileName = file.getName();
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                saveTask.run();
                //Status line
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ProjectControllerUIImpl.class, "ProjectControllerUI.status.saved", fileName));
            }
        };
        if (saveTask instanceof LongTask) {
            longTaskExecutor.execute((LongTask) saveTask, saveRunnable);
        } else {
            longTaskExecutor.execute(null, saveRunnable);
        }

        //Save MRU
        MostRecentFiles mostRecentFiles = Lookup.getDefault().lookup(MostRecentFiles.class);
        mostRecentFiles.addFile(file.getAbsolutePath());
    }

    @Override
    public void saveProject() {
        Project project = controller.getCurrentProject();
        if (project.getLookup().lookup(ProjectInformation.class).hasFile()) {
            File file = project.getLookup().lookup(ProjectInformation.class).getFile();
            saveProject(project, file);
        } else {
            saveAsProject();
        }
    }

    @Override
    public void saveAsProject() {
        final String LAST_PATH = "SaveAsProject_Last_Path";
        final String LAST_PATH_DEFAULT = "SaveAsProject_Last_Path_Default";

        DialogFileFilter filter = new DialogFileFilter(NbBundle.getMessage(ProjectControllerUIImpl.class, "SaveAsProject_filechooser_filter"));
        filter.addExtension("."+ PROJECT_EXTENSION);

        //Get last directory
        String lastPathDefault = NbPreferences.forModule(ProjectControllerUIImpl.class).get(LAST_PATH_DEFAULT, null);
        String lastPath = NbPreferences.forModule(ProjectControllerUIImpl.class).get(LAST_PATH, lastPathDefault);

        //File chooser
        final JFileChooser chooser = new JFileChooser(lastPath);
        chooser.addChoosableFileFilter(filter);
        int returnFile = chooser.showSaveDialog(null);
        if (returnFile == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            //Save last path
            NbPreferences.forModule(ProjectControllerUIImpl.class).put(LAST_PATH, file.getAbsolutePath());

            //File management
            try {
                if (!file.getPath().endsWith(PROJECT_EXTENSION)) {
                    file = new File(file.getPath() + PROJECT_EXTENSION);
                }
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        String failMsg = NbBundle.getMessage(
                                ProjectControllerUIImpl.class,
                                "SaveAsProject_SaveFailed", new Object[]{file.getPath()});
                        JOptionPane.showMessageDialog(null, failMsg);
                        return;
                    }
                } else {
                    String overwriteMsg = NbBundle.getMessage(
                            ProjectControllerUIImpl.class,
                            "SaveAsProject_Overwrite", new Object[]{file.getPath()});
                    if (JOptionPane.showConfirmDialog(null, overwriteMsg) != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                file = FileUtil.normalizeFile(file);
                final String SaveAsFileName = file.getName();
                //File exist now, Save project
                Project project = controller.getCurrentProject();
                saveProject(project, file);

                //Modifying Title bar
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                        String title = frame.getTitle();
                        title = title.substring(0, title.indexOf('-') - 1) + " - " + SaveAsFileName;
                        frame.setTitle(title);
                    }
                });

            } catch (Exception e) {
                Logger.getLogger("").log(Level.WARNING, "", e);
            }
        }
    }

    public boolean closeCurrentProject() {
        if (controller.getCurrentProject() != null) {

            //Save ?
            String messageBundle = NbBundle.getMessage(ProjectControllerUIImpl.class, "CloseProject_confirm_message");
            String titleBundle = NbBundle.getMessage(ProjectControllerUIImpl.class, "CloseProject_confirm_title");
            String saveBundle = NbBundle.getMessage(ProjectControllerUIImpl.class, "CloseProject_confirm_save");
            String doNotSaveBundle = NbBundle.getMessage(ProjectControllerUIImpl.class, "CloseProject_confirm_doNotSave");
            String cancelBundle = NbBundle.getMessage(ProjectControllerUIImpl.class, "CloseProject_confirm_cancel");
            NotifyDescriptor msg = new NotifyDescriptor(messageBundle, titleBundle,
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.INFORMATION_MESSAGE,
                    new Object[]{saveBundle, doNotSaveBundle, cancelBundle}, saveBundle);
            Object result = DialogDisplayer.getDefault().notify(msg);
            if (result == saveBundle) {
                saveProject();
            } else if (result == cancelBundle) {
                return false;
            }

            controller.closeCurrentProject();

            //Actions
            saveProject = false;
            saveAsProject = false;
            projectProperties = false;
            closeProject = false;
            newWorkspace = false;
            deleteWorkspace = false;
            cleanWorkspace = false;
            duplicateWorkspace = false;

            //Title bar
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = frame.getTitle();
                    title = title.substring(0, title.indexOf('-') - 1);
                    frame.setTitle(title);
                }
            });
        }
        return true;
    }

    @Override
    public void openProject(File file) {
        if (controller.getCurrentProject() != null) {
            if (!closeCurrentProject()) {
                return;
            }
        }
        loadProject(file);
    }

    private void loadProject(File file) {
        lockProjectActions();

        final Runnable loadTask = controller.openProject(file);
        final String fileName = file.getName();
        Runnable loadRunnable = new Runnable() {
            @Override
            public void run() {
                loadTask.run();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                        String title = frame.getTitle() + " - " + fileName;
                        frame.setTitle(title);
                    }
                });
                //Status line
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ProjectControllerUIImpl.class, "ProjectControllerUI.status.opened", fileName));
            }
        };
        if (loadTask instanceof LongTask) {
            longTaskExecutor.execute((LongTask) loadTask, loadRunnable);
        } else {
            longTaskExecutor.execute(null, loadRunnable);
        }

        //Save MRU
        MostRecentFiles mostRecentFiles = Lookup.getDefault().lookup(MostRecentFiles.class);
        mostRecentFiles.addFile(file.getAbsolutePath());
    }

    @Override
    public void renameProject(final String name) {
        controller.renameProject(controller.getCurrentProject(), name);

        //Title bar
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                String title = frame.getTitle();
                title = title.substring(0, title.indexOf('-') - 1);
                title += " - " + name;
                frame.setTitle(title);
            }
        });
    }

    public boolean canCleanWorkspace() {
        return cleanWorkspace;
    }

    @Override
    public boolean canCloseProject() {
        return closeProject;
    }

    public boolean canDeleteWorkspace() {
        return deleteWorkspace;
    }

    @Override
    public boolean canNewProject() {
        return newProject;
    }

    public boolean canNewWorkspace() {
        return newWorkspace;
    }

    public boolean canDuplicateWorkspace() {
        return duplicateWorkspace;
    }

    @Override
    public boolean canOpenFile() {
        return openFile;
    }

    @Override
    public boolean canSave() {
        return saveProject;
    }

    @Override
    public boolean canSaveAs() {
        return saveAsProject;
    }

    @Override
    public boolean canProjectProperties() {
        return projectProperties;
    }

    private void lockProjectActions() {
        saveProject = false;
        saveAsProject = false;
        openProject = false;
        closeProject = false;
        newProject = false;
        openFile = false;
        newWorkspace = false;
        deleteWorkspace = false;
        cleanWorkspace = false;
        duplicateWorkspace = false;
    }

    private void unlockProjectActions() {
        if (controller.getCurrentProject() != null) {
            saveProject = true;
            saveAsProject = true;
            closeProject = true;
            newWorkspace = true;
            projectProperties = true;
            if (controller.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).hasCurrentWorkspace()) {
                deleteWorkspace = true;
                cleanWorkspace = true;
                duplicateWorkspace = true;
            }
        }
        openProject = true;
        newProject = true;
        openFile = true;
    }

    @Override
    public void projectProperties() {
        Project project = controller.getCurrentProject();
        ProjectPropertiesUI ui = Lookup.getDefault().lookup(ProjectPropertiesUI.class);
        if (ui != null) {
            JPanel panel = ui.getPanel();
            ui.setup(project);
            DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(ProjectControllerUIImpl.class, "ProjectProperties_dialog_title"));
            Object result = DialogDisplayer.getDefault().notify(dd);
            if (result == NotifyDescriptor.OK_OPTION) {
                ui.unsetup(project);
            }
        }
    }

    @Override
    public void openFile() {
        final String LAST_PATH = "OpenFile_Last_Path";
        final String LAST_PATH_DEFAULT = "OpenFile_Last_Path_Default";

        //Get last directory
        String lastPathDefault = NbPreferences.forModule(ProjectControllerUIImpl.class).get(LAST_PATH_DEFAULT, null);
        String lastPath = NbPreferences.forModule(ProjectControllerUIImpl.class).get(LAST_PATH, lastPathDefault);

        //Init dialog
        final JFileChooser chooser = new JFileChooser(lastPath);
        DialogFileFilter clueminerFilter = new DialogFileFilter(NbBundle.getMessage(ProjectControllerUIImpl.class, "OpenProject_filechooser_filter"));
        clueminerFilter.addExtension("."+PROJECT_EXTENSION);

        ImportControllerUI importController = Lookup.getDefault().lookup(ImportControllerUI.class);
        for (FileType fileType : importController.getImportController().getFileTypes()) {
            DialogFileFilter dialogFileFilter = new DialogFileFilter(fileType.getName());
            dialogFileFilter.addExtensions(fileType.getExtensions());
            chooser.addChoosableFileFilter(dialogFileFilter);

        }
        DialogFileFilter zipFileFilter = new DialogFileFilter(NbBundle.getMessage(getClass(), "OpenFile_filechooser_zipfilter"));
        zipFileFilter.addExtensions(new String[]{".zip", ".gz", ".bz2"});
        chooser.addChoosableFileFilter(zipFileFilter);
        chooser.addChoosableFileFilter(clueminerFilter);

        //Open dialog
        int returnFile = chooser.showOpenDialog(null);

        if (returnFile == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            file = FileUtil.normalizeFile(file);
            FileObject fileObject = FileUtil.toFileObject(file);
            if(fileObject == null){
                System.out.println("failed to converto to file object");
            }

            //Save last path
            NbPreferences.forModule(ProjectControllerUIImpl.class).put(LAST_PATH, file.getAbsolutePath());

            if (fileObject.getExt().equalsIgnoreCase(PROJECT_EXTENSION)) {
                //Project
                if (controller.getCurrentProject() != null) {
                    if (!closeCurrentProject()) {
                        return;
                    }
                }

                try {
                    loadProject(file);
                } catch (Exception ew) {
                    Exceptions.printStackTrace(ew);
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message(NbBundle.getMessage(ProjectControllerUIImpl.class, "OpenProject.defaulterror"), NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(msg);
                }
            } else {
                //Import
                importController.importFile(fileObject);
            }
        }
    }

    @Override
    public Project newProject() {
        if (closeCurrentProject()) {
            controller.newProject();
            final Project project = controller.getCurrentProject();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = frame.getTitle() + " - " + project.getLookup().lookup(ProjectInformation.class).getName();
                    frame.setTitle(title);
                }
            });

            unlockProjectActions();
            return project;
        }
        return null;
    }

    @Override
    public void closeProject() {
        if (closeCurrentProject()) {
            controller.closeCurrentProject();
        }
    }

    public Workspace newWorkspace() {
        return controller.newWorkspace(controller.getCurrentProject());
    }

    public void cleanWorkspace() {
        controller.cleanWorkspace(controller.getCurrentWorkspace());
    }

    public void deleteWorkspace() {
        if (controller.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces().length == 1) {
            //Close project
            //Actions
            saveProject = false;
            saveAsProject = false;
            projectProperties = false;
            closeProject = false;
            newWorkspace = false;
            deleteWorkspace = false;
            cleanWorkspace = false;
            duplicateWorkspace = false;

            //Title bar
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = frame.getTitle();
                    title = title.substring(0, title.indexOf('-') - 1);
                    frame.setTitle(title);
                }
            });
        }
        controller.deleteWorkspace(controller.getCurrentWorkspace());
    }

    public void renameWorkspace(String name) {
        controller.renameWorkspace(controller.getCurrentWorkspace(), name);
    }

    public Workspace duplicateWorkspace() {
        return controller.duplicateWorkspace(controller.getCurrentWorkspace());
    }

    @Override
    public boolean isFileSupported(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
