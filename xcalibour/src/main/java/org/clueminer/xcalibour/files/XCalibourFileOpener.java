package org.clueminer.xcalibour.files;

import eu.medsea.mimeutil.MimeUtil2;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.clueminer.xcalibour.plot3d.XCalibour3dTopComponent;
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
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 80)
public class XCalibourFileOpener implements OpenFileImpl, TaskListener {

    private MimeUtil2 mimeUtil = new MimeUtil2();
    private XCalibourImporter importer;
    private static Project project;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public XCalibourFileOpener() {
        //MIME type detection
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
    }

    protected Collection detectMIME(File file) {
        Collection mimeTypes = null;
        try {
            byte[] data;
            InputStream in = new FileInputStream(file);
            int bytes = 1024;
            data = new byte[bytes];
            in.read(data, 0, bytes);
            in.close();
            mimeTypes = mimeUtil.getMimeTypes(data);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return mimeTypes;
    }

    /**
     * MIME type is too broad therefore we check extensions
     *
     * @TODO improve MIME detection (perhaps add some CDF type?)
     * @param f
     * @return
     */
    protected boolean openFile(File f) {
        Collection mimeTypes = detectMIME(f);
        String ext = getExtension(f.getPath());
        String mime = mimeTypes.toString();
        if ((ext.equals("cdf") || ext.equals("tmp")) && mime.contains("octet-stream")) {
            importer = new XCalibourImporter(f);
            openXCalibourFile(importer);
            return true;
        }
        return false;
    }

    @Override
    public boolean open(FileObject fileObject) {
        File f = FileUtil.toFile(fileObject);
        return openFile(f);
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

    @Override
    public void taskFinished(Task task) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                System.out.println("opening task finished");
                ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                project.add(importer.getDataset());
                importer.getDataset().setName(importer.getFile().getName());

                XCalibour3dTopComponent tc = new XCalibour3dTopComponent();
                tc.setDataset(importer.getDataset());
                //tc.setProject(project);
                //tc.setDisplayName(plate.getName());
                tc.open();
                tc.requestActive();
                /*
                 // flying square visualization
                 Plot2dTopComponent plot = new Plot2dTopComponent();
                //plot.setDataset(importer.getDataset());                
                plot.open();
                plot.requestActive();
*/

                pc.openProject(project);
                Workspace workspace = pc.getCurrentWorkspace();
                if (workspace != null) {
                    System.out.println("workspace: " + workspace.toString());
                    System.out.println("adding plate to lookup");
                    workspace.add(importer.getDataset());  //add plate to project's lookup
                } else {
                    System.out.println("workspace is null!!!!");
                }

                //     DataPreprocessing preprocess = new DataPreprocessing(plate, tc);
                //     preprocess.start();
            }
        });

    }

    protected void openXCalibourFile(XCalibourImporter importer) {
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
