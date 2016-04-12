package org.clueminer.spi;

import java.io.IOException;
import java.io.Reader;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.openide.filesystems.FileObject;

/**
 * Interface for classes which imports data from files, databases, streams or
 * other sources.
 * <p>
 * Importers are built from {@link ImporterBuilder} services and can be
 * configured
 * by {@link ImporterUI} classes.
 *
 * @see ImportController
 */
public interface Importer<E extends InstanceDraft> {

    /**
     * Should be unique importer ID
     *
     * @return importer identification
     */
    String getName();

    /**
     * Tries to import data from reader into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param reader
     * @return
     * @throws java.io.IOException
     */
    boolean execute(Container<E> container, Reader reader) throws IOException;

    /**
     * Tries to import data from reader into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param reader
     * @param limit     when > 1 number of lines read will be limited
     * @return
     * @throws java.io.IOException
     */
    boolean execute(Container<E> container, Reader reader, int limit) throws IOException;

    /**
     * Tries to import data from FileObject into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param file
     * @return
     * @throws IOException
     */
    boolean execute(Container<E> container, FileObject file) throws IOException;

    /**
     * Returns the import container. The container is the import "result", all
     * data found during import are being pushed to the container.
     *
     * @return the import container
     */
    Container getContainer();

    /**
     * Returns the import report, filled with logs and potential issues.
     *
     * @return the import report
     */
    Report getReport();
}
