package org.clueminer.openfile;

import org.openide.filesystems.FileObject;

/**
 * Interface for Open File implementations.
 *
 * @author Tomas Barton
 */
public interface OpenFileImpl {

    /**
     * Opens the specified
     * <code>FileObject</code>.
     *
     * @param fileObject file to open
     * @return true on success, false on failure
     */
    boolean open(FileObject fileObject);
}
