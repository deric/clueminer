package org.clueminer.spi;

import java.io.File;
import java.io.Reader;
import java.util.Collection;
import org.clueminer.types.FileType;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Barton
 */
public interface FileImporter extends Importer {

    /**
     * Return true if importer supports given MIME type
     *
     * @param mimeTypes Collection of String, String[] and MimeType objects
     * @return
     */
    boolean isAccepting(Collection mimeTypes);

    /**
     * Get default file types this importer can deal with.
     *
     * @return an array of file types this importer can read
     */
    FileType[] getFileTypes();

    /**
     * Returns <code>true</code> if this importer can import
     * <code>fileObject</code>. Called from
     * controllers to identify dynamically which importers can be used for a
     * particular file format.
     * <p>
     * Use <code>FileObject.getExt()</code> to retrieve file extension. Matching
     * can be done not only with
     * metadata but also with file content. The <code>fileObject</code> can be
     * read in that way.
     *
     * @param fileObject the file in input
     * @return <code>true</code> if the importer is compatible with
     *         <code>fileObject</code> or <code>false</code>
     *         otherwise
     */
    boolean isMatchingImporter(FileObject fileObject);

    /**
     * Reload import (with new importer settings)
     *
     * @param file
     */
    void reload(File file);

    /**
     *
     * @param file
     * @param reader
     */
    void reload(final FileObject file, Reader reader);

    /**
     * Add listener to events invoked by importer (pre-loading data finished
     * etc.)
     *
     * @param listener
     */
    void addAnalysisListener(AnalysisListener listener);

    /**
     * Remove listener
     *
     * @param listener
     */
    void removeListener(AnalysisListener listener);

}
