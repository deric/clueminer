package org.clueminer.importer;

import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.spi.ImportListener;

/**
 *
 * @author Tomas Barton
 */
public interface ImportTask extends Runnable {

    ContainerLoader getContainer();

    /**
     * Adds import listener
     *
     * @param listener
     */
    void addListener(ImportListener listener);

    /**
     * Removes import listener
     *
     * @param listener
     */
    void removeListener(ImportListener listener);
}
