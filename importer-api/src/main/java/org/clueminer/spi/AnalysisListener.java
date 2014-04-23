package org.clueminer.spi;

import java.util.EventListener;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;

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
    void analysisFinished(Container container);

    /**
     * Some attribute property got changed due to auto-detection
     *
     * @param attr
     * @param property
     */
    void attributeChanged(AttributeDraft attr, Object property);
}
