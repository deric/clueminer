package org.clueminer.spi;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface ImportListener extends EventListener {

    /**
     * Called when importer or some of its parameters were changed
     *
     * @param importer
     * @param importerUI
     */
    void importerChanged(Importer importer, ImporterUI importerUI);

}
