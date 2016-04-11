package org.clueminer.importer;

import org.clueminer.io.importer.api.Container;
import org.clueminer.spi.ImportListener;

/**
 * Possibly long running task of data import.
 *
 * @author Tomas Barton
 */
public interface ImportTask extends Runnable {

    Container getContainer();

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
