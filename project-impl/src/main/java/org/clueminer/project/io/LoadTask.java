package org.clueminer.project.io;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.File;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
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

    /**
     * {@link FilterReader} to skip invalid xml version 1.0 characters. Valid
     * Unicode chars for xml version 1.0 according to http://www.w3.org/TR/xml
     * are #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD], [#x10000-#x10FFFF]
     * . In other words - any Unicode character, excluding the surrogate blocks,
     * FFFE, and FFFF. <p> More details on the <a
     * href="http://info.tsachev.org/2009/05/skipping-invalid-xml-character-with.html">blog</a>
     */
    public class Xml10FilterReader extends FilterReader {

        /**
         * Creates filter reader which skips invalid xml characters.
         *
         * @param in original reader
         */
        public Xml10FilterReader(Reader in) {
            super(in);
        }

        /**
         * Every overload of {@link Reader#read()} method delegates to this one
         * so it is enough to override only this one. <br /> To skip invalid
         * characters this method shifts only valid chars to left and returns
         * decreased value of the original read method. So after last valid
         * character there will be some unused chars in the buffer.
         *
         * @return Number of read valid characters or <code>-1</code> if end of
         * the underling reader was reached.
         */
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int read = super.read(cbuf, off, len);
            /*
             * If read chars are -1 then we have reach the end of the reader.
             */
            if (read == -1) {
                return -1;
            }
            /*
             * pos will show the index where chars should be moved if there are gaps
             * from invalid characters.
             */
            int pos = off - 1;

            for (int readPos = off; readPos < off + read; readPos++) {
                if (XMLChar.isValid(cbuf[readPos])) {
                    pos++;
                } else {
                    continue;
                }
                /*
                 * If there is gap(s) move current char to its position.
                 */
                if (pos < readPos) {
                    cbuf[pos] = cbuf[readPos];
                }
            }
            /*
             * Number of read valid characters.
             */
            return pos - off + 1;
        }
    }
}
