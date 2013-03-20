package org.clueminer.events;

import java.util.EventListener;

/**
 * Events connected to loading to data files
 * 
 * @author Tomas Barton
 */
public interface DatasetListener extends EventListener {

    public void datasetChanged(DatasetEvent evt);
    
    public void datasetOpened(DatasetEvent evt);
    
    public void datasetClosed(DatasetEvent evt);
    
    public void datasetCropped(DatasetEvent evt);
}
