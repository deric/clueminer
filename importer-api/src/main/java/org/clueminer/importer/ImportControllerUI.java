package org.clueminer.importer;

import java.io.InputStream;
import java.io.Reader;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Barton
 */
public interface ImportControllerUI {

    public void importFile(FileObject fileObject);

    public void importStream(InputStream stream, String importerName);

    public void importFile(Reader reader, String importerName);

    public ImportController getImportController();
}
