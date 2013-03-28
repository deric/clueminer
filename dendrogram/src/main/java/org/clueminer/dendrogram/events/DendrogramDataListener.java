package org.clueminer.dendrogram.events;

import java.util.EventListener;
import org.clueminer.dendrogram.DendrogramData;

/**
 *
 * @author Tomas Barton
 */
public interface DendrogramDataListener extends EventListener {

    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset);

    /**
     * 
     * @param evt
     * @param width new element width
     * @param isAdjusting when true user is changing the value with some slider,
     * so we should draw the result as fast as possible
     */
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting);
    
    /**
     * 
     * @param evt
     * @param height
     * @param isAdjusting when true user is changing the value with some slider,
     * so we should draw the result as fast as possible
     */
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting);
}
