package org.clueminer.processor.spi;

import org.clueminer.project.api.Workspace;

/**
 *
 * @author Tomas Barton
 */
public class AbstractProcessor {
    protected Workspace workspace;

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
