package org.clueminer.project.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.project.api.Project;
import org.clueminer.utils.progress.Progress;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Tomas Barton
 */
public class SaveTask implements LongTask, Runnable {

    private static final String ZIP_LEVEL_PREFERENCE = "ProjectIO_Save_ZipLevel_0_TO_9";
    private File file;
    private Project project;
    private ClueminerWriter clmWriter;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    public SaveTask(Project project, File file) {
        this.project = project;
        this.file = file;
    }

    @Override
    public void run() {
        //System.out.println("Save " + dataObject.getName());
        ZipOutputStream zipOut = null;
        BufferedOutputStream bufferedOutputStream = null;
        boolean useTempFile = false;
        File writeFile = null;
        try {
            Progress.start(progressTicket);
            Progress.setDisplayName(progressTicket, NbBundle.getMessage(SaveTask.class, "SaveTask.name"));
            FileObject fileObject = FileUtil.toFileObject(file);
            writeFile = file;
            if (writeFile.exists()) {
                useTempFile = true;
                String tempFileName = writeFile.getName() + "_temp";
                writeFile = new File(writeFile.getParent(), tempFileName);
            }

            //Stream
            int zipLevel = NbPreferences.forModule(SaveTask.class).getInt(ZIP_LEVEL_PREFERENCE, 9);
            FileOutputStream outputStream = new FileOutputStream(writeFile);
            zipOut = new ZipOutputStream(outputStream);
            zipOut.setLevel(zipLevel);

            zipOut.putNextEntry(new ZipEntry("Project"));
            clmWriter = new ClueminerWriter();

            //Create Writer and write project
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
            bufferedOutputStream = new BufferedOutputStream(zipOut);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bufferedOutputStream, "UTF-8");
            clmWriter.writeAll(project, writer);
            writer.close();

            //Close
            zipOut.closeEntry();
            zipOut.finish();
            bufferedOutputStream.close();



            //Clean and copy
            if (useTempFile && !cancel) {
                String name = fileObject.getName();
                String ext = fileObject.getExt();

                //Delete original file
                fileObject.delete();

                //Rename temp file
                FileObject tempFileObject = FileUtil.toFileObject(writeFile);
                FileLock lock = tempFileObject.lock();
                tempFileObject.rename(lock, name, ext);
                lock.releaseLock();
            } else if (cancel) {
                //Delete temp file
                FileObject tempFileObject = FileUtil.toFileObject(writeFile);
                tempFileObject.delete();
            }
            Progress.finish(progressTicket);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException ex1) {
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException ex1) {
                }
            }
            if (useTempFile && writeFile != null) {
                writeFile.delete();
            }
            throw new ClueminerFormatException(ClueminerWriter.class, ex);
        }
    }

    @Override
    public boolean cancel() {
        if (clmWriter != null) {
            clmWriter.cancel();
        }
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
