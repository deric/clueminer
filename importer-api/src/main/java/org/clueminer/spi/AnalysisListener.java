package org.clueminer.spi;

import java.util.EventListener;
import org.clueminer.io.importer.api.ContainerLoader;

/**
 *
 * @author Tomas Barton
 */
public interface AnalysisListener extends EventListener {

    /**
     * Finished pre-loading data
     *
     * @param container
     */
    void analysisFinished(ContainerLoader container);
}
