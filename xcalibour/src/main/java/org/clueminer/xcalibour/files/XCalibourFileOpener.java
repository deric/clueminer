package org.clueminer.xcalibour.files;

import eu.medsea.mimeutil.MimeUtil2;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 150)
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
        /**
         * @TODO implement opening tabs
         */
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
