package org.clueminer.spi;

import java.io.File;
import java.io.Reader;
import java.util.Collection;

/**
 *
 * @author Tomas Barton
 */
public interface FileImporter extends Importer {

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
}
