package org.clueminer.importer;

import org.clueminer.io.importer.api.ContainerLoader;

/**
 *
 * @author Tomas Barton
 */
public interface ImportTask extends Runnable {

    ContainerLoader getContainer();
}
