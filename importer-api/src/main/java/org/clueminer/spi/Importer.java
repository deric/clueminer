package org.clueminer.spi;

import org.clueminer.io.importer.api.ContainerUnloader;
import org.clueminer.types.ContainerLoader;
import org.clueminer.io.importer.api.Report;

/**
 * Interface for classes which imports data from files, databases, streams or other sources.
 * <p>
 * Importers are built from {@link ImporterBuilder} services and can be configured
 * by {@link ImporterUI} classes.
 *
 * @author Mathieu Bastian
 * @see ImportController
 */
public interface Importer {

    /**
     *
     * @return name of the importer
     */
    String getName();

    /**
     * Run the import process
     * @param loader    the container where imported data will be pushed
     * @return          <code>true</code> if the import is successful or
     *                  <code>false</code> if it has been canceled
     */
    boolean execute(ContainerLoader loader);

    /**
     * Returns the import container. The container is the import "result", all
     * data found during import are being pushed to the container.
     * @return          the import container
     */
    ContainerLoader getContainer();

    /**
     *
     * @return
     */
    ContainerUnloader getUnloader();

    /**
     * Returns the import report, filled with logs and potential issues.
     * @return          the import report
     */
    Report getReport();
}

