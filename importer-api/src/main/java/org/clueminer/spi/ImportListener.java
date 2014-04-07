package org.clueminer.spi;

/**
 *
 * @author Tomas Barton
 */
public interface ImportListener {

    /**
     * Called when importer or some of its parameters were changed
     *
     * @param importer
     */
    void importerChanged(Importer importer);

}
