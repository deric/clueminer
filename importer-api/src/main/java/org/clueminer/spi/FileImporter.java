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
     * Should be unique importer ID
     *
     * @return importer identification
     */
    public String getName();

    /**
     * Sets the reader where characters can be retrieved.
     *
     * @param reader the reader on data
     */
    public void setReader(Reader reader);

    /**
     * Return File which is supposed to be processed
     *
     * @return File
     */
    public File getFile();

    /**
     * Sets File to process
     *
     * @param file
     */
    public void setFile(File file);

    /**
     * Return true if importer supports given MIME type
     *
     * @param mimeTypes Collection of String, String[] and MimeType objects
     * @return
     */
    public boolean isAccepting(Collection mimeTypes);

    /**
     * Get default file types this importer can deal with.
     *
     * @return an array of file types this importer can read
     */
    public FileType[] getFileTypes();

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
    public boolean isMatchingImporter(FileObject fileObject);
}
