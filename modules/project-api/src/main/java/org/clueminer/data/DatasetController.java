package org.clueminer.data;

import org.clueminer.project.api.Workspace;


/**
 *
 * @author Tomas Barton
 */
public interface DatasetController {

    /**
     * Return DatasetModel for current workspace
     *
     * @return current DatasetModel
     */
    public DatasetModel getModel();
    
    
    public DatasetModel getModel(Workspace workspace);
}
