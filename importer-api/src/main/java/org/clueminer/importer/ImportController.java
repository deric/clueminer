package org.clueminer.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import org.clueminer.types.FileType;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Database;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.DatabaseImporter;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.clueminer.spi.ImporterWizardUI;

/**
 *
 * @author Tomas Barton
 */
public interface ImportController {

    public Container importFile(File file) throws FileNotFoundException;

    public Container importFile(File file, FileImporter importer) throws FileNotFoundException;

    /**
     *
     * @param reader
     * @param importer
     * @param reload   true when reloading same file
     * @return
     */
    public Container importFile(Reader reader, FileImporter importer, boolean reload);

    /**
     *
     * @param stream
     * @param importer
     * @param reload   true when reloading same file
     * @return
     */
    public Container importFile(InputStream stream, FileImporter importer, boolean reload);

    public FileImporter getFileImporter(File file);

    public FileImporter getFileImporter(String importerName);

    public void process(Container container);

    public void process(Container container, Processor processor, Workspace workspace);

    public FileType[] getFileTypes();

    /**
     * Checks support by extension
     *
     * @param file
     * @return
     */
    public boolean isFileSupported(File file);

    /**
     * Checks if importers support given MIME type
     *
     * @param file
     * @return true when MIME type is supported by at least one importer
     */
    public boolean isAccepting(File file);

    public ImporterUI getUI(Importer importer);

    public ImporterWizardUI getWizardUI(Importer importer);

    public Container importDatabase(Database database, DatabaseImporter importer);
}
