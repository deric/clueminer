package org.clueminer.dataset.api;

import java.awt.Graphics;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface Plotter extends Serializable {

    public void addInstance(Instance instance);

    public void paint(Graphics g);
    
    /**
     * Clear all currently painted data
     */
    public void clearAll();
}
