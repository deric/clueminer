package org.clueminer.importer.impl;

import eu.medsea.mimeutil.MimeUtil2;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class MimeHelper {

    private final MimeUtil2 mimeUtil = new MimeUtil2();

    public MimeHelper() {
        //MIME type detection
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
    }

    public Collection detectMIME(FileObject file) {
        return detectMIME(FileUtil.toFile(file));
    }

    /**
     * We can't use fileObject directly because fileObject doesn't work with test
     * fixtures.
     *
     * @param file
     * @return
     */
    public Collection detectMIME(File file) {
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

}
