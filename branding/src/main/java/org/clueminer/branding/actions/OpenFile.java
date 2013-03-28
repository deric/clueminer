package org.clueminer.branding.actions;

import org.clueminer.openfile.OpenFileImpl;
import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Opens files when requested. Main functionality.
 * @author Jaroslav Tulach, Jesse Glick
 * @author Marian Petras
 */
public final class OpenFile {

    /** do not instantiate */
    private OpenFile() {}

    /**
     * Open a file (object) at the beginning.
     * @param fileObject the file to open
     * @param line 
     * @return error message or null on success
     * @usecase  API
     */
    public static String open(FileObject fileObject) {
        for (OpenFileImpl impl : Lookup.getDefault().lookupAll(OpenFileImpl.class)) {
            System.out.println("trying opening with "+impl.getClass().toString());
            if (impl.open(fileObject)) {
                return null;
            }
        }
        return NbBundle.getMessage(OpenFile.class, "MSG_FileIsNotPlainFile", fileObject);
    }
    
    /**
     * Opens a file.
     *
     * @param  file  file to open (must exist)
     * @return null on success, otherwise the error message
     * @usecase CallbackImpl, OpenFileAction
     */
    static String openFile(File file) {
        String msg = checkFileExists(file);
        if (msg != null) {
            return msg;
        }
                              
        FileObject fileObject;
        fileObject = FileUtil.toFileObject(FileUtil.normalizeFile(file));
        if (fileObject != null) {
            return open(fileObject);
        }
        return NbBundle.getMessage(OpenFile.class, "MSG_FileDoesNotExist", file);
    }
    
    /**
     * Checks whether the specified file exists.
     * If the file doesn't exists, displays a message.
     * <p>
     * The code for displaying the message is running in a separate thread
     * so that it does not block the current thread.
     *
     * @param  file  file to check for existence
     * @return  null on success, otherwise the error message
     */
    private static String checkFileExists(File file) {
        if (!file.exists() || (!file.isFile() && !file.isDirectory())) {
            return NbBundle.getMessage(OpenFile.class, "MSG_fileNotFound", file.toString());  //NOI18N
        } else {
            return null;
        }
    }
}
