package org.clueminer.processor;

import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.Workspace;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractProcessor implements Processor {

    protected Workspace workspace;
    protected ContainerLoader container;

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void setContainer(ContainerLoader container) {
        this.container = container;
    }

}
