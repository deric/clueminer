package org.clueminer.importer;

import java.io.InputStream;
import java.io.Reader;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Barton
 */
public interface ImportControllerUI {

    public ImportTask importFile(FileObject fileObject);

    public ImportTask importStream(InputStream stream, String importerName);

    public ImportTask importFile(Reader reader, String importerName);

    public ImportController getImportController();
}
