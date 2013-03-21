package org.clueminer.clustering.api.dendrogram;

import java.awt.Graphics;

/**
 *
 * @author Tomas Barton
 */
public interface DendrogramTree {
    
    public void paint(Graphics g);
    
    public void updateSize();
    
    public int getMinDistance();
    
    public int getMaxDistance();
    
}
