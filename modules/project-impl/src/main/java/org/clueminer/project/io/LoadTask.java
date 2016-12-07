package org.clueminer.project.io;

import java.io.File;
import java.io.InputStreamReader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.project.impl.ProjectControllerImpl;
import org.clueminer.project.impl.ProjectImpl;
import org.clueminer.project.impl.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.utils.progress.Progress;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class LoadTask implements LongTask, Runnable {

    private File file;
    private ClueminerReader clReader;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    public LoadTask(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            Progress.start(progressTicket);
            Progress.setDisplayName(progressTicket, NbBundle.getMessage(LoadTask.class, "LoadTask.name"));
            FileObject fileObject = FileUtil.toFileObject(file);
            if (FileUtil.isArchiveFile(fileObject)) {
                //Unzip
                fileObject = FileUtil.getArchiveRoot(fileObject).getChildren()[0];
            }

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            if (inputFactory.isPropertySupported("javax.xml.stream.isValidating")) {
                inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
            }
            inputFactory.setXMLReporter(new XMLReporter() {
                @Override
                public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException {
                    System.out.println("Error:" + errorType + ", message : " + message);
                }
            });
            InputStreamReader isReader = new InputStreamReader(fileObject.getInputStream(), "UTF-8");
            Xml10FilterReader filterReader = new Xml10FilterReader(isReader);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(filterReader);

            if (!cancel) {
                //Project instance
                Project project = new ProjectImpl();
                project.getLookup().lookup(ProjectInformationImpl.class).setFile(file);

                clReader = new ClueminerReader();
                project = clReader.readAll(reader, project);

                //Add project
                if (!cancel) {
                    ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                    pc.openProject(project);
                }
            }
            Progress.finish(progressTicket);
        } catch (Exception ex) {
            if (ex instanceof ClueminerFormatException) {
                throw (ClueminerFormatException) ex;
            }
            throw new ClueminerFormatException(ClueminerReader.class, ex);
        }
    }

    @Override
    public boolean cancel() {
        cancel = true;
        if (clReader != null) {
            clReader.cancel();
        }
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

}
