package org.clueminer.mlearn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.dendrogram.DendrogramTopComponent;
import org.clueminer.importer.ImportController;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 90)
public class MLearnFileOpener implements OpenFileImpl, TaskListener {

    private static Project project;
    private MLearnImporter importer;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private final ImportController controller = Lookup.getDefault().lookup(ImportController.class);

    public MLearnFileOpener() {

    }


    /**
     * Return true is file seems to be in format which is supported by this
     * package
     *
     * @param f
     * @return boolean
     * @throws java.io.FileNotFoundException
     */
    protected boolean isFileSupported(File f) throws FileNotFoundException, IOException {
        return controller.isFileSupported(f);
    }

    @Override
    public boolean open(FileObject fileObject) {
        File f = FileUtil.toFile(fileObject);
        try {
            if (isFileSupported(f)) {

                importer = new MLearnImporter(f);
                openDataFile(importer);
                return true;
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    protected String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i + 1);
        }

        return extension;
    }

    protected String getTitle(String filename) {
        String title = filename;
        int pos = filename.lastIndexOf('.');
        if (pos > -1) {
            title = filename.substring(0, pos).trim();
        }
        return title;
    }

    @Override
    public void taskFinished(Task task) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                project.add(importer.getDataset());
                String filename = importer.getFile().getName();
                importer.getDataset().setName(filename);

                DendrogramTopComponent tc = new DendrogramTopComponent();

                tc.setDataset(importer.getDataset());
                //tc.setProject(project);
                tc.setDisplayName(getTitle(filename));
                tc.open();
                tc.requestActive();

                pc.openProject(project);
                Workspace workspace = pc.getCurrentWorkspace();
                if (workspace != null) {
                    workspace.add(importer.getDataset());  //add plate to project's lookup
                }
                //     DataPreprocessing preprocess = new DataPreprocessing(plate, tc);
                //     preprocess.start();
            }
        });

    }

    protected void openDataFile(MLearnImporter importer) {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Opening file " + importer.getFile().getName());
        importer.setProgressHandle(ph);
        //Project instance
        project = new ProjectImpl();
        project.getLookup().lookup(ProjectInformationImpl.class).setFile(importer.getFile());
        final RequestProcessor.Task task = RP.create(importer);
        task.addTaskListener(this);
        task.schedule(0);
    }
}
