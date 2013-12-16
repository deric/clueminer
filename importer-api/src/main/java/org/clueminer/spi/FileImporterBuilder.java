package org.clueminer.spi;

import org.clueminer.types.FileType;
import org.openide.filesystems.FileObject;

/**
 * Importer builder specific for {@link FileImporter}.
 * 
 * @author Mathieu Bastian
 */
public interface FileImporterBuilder extends ImporterBuilder {

    /**
     * Builds a new file importer instance, ready to be used.
     * @return  a new file importer
     */
    @Override
    public FileImporter buildImporter();

    /**
     * Get default file types this importer can deal with.
     * @return an array of file types this importer can read
     */
    public FileType[] getFileTypes();

    /**
     * Returns <code>true</code> if this importer can import <code>fileObject</code>. Called from
     * controllers to identify dynamically which importers can be used for a particular file format.
     * <p>
     * Use <code>FileObject.getExt()</code> to retrieve file extension. Matching can be done not only with
     * metadata but also with file content. The <code>fileObject</code> can be read in that way.
     * @param fileObject the file in input
     * @return <code>true</code> if the importer is compatible with <code>fileObject</code> or <code>false</code>
     * otherwise
     */
    public boolean isMatchingImporter(FileObject fileObject);
}
