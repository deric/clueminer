package org.clueminer.spi;

import org.clueminer.project.api.Workspace;

/**
 *
 * @author Tomas Barton
 */
public interface WorkspaceDuplicateProvider {

    public void duplicate(Workspace source, Workspace destination);
}
