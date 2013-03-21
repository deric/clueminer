package org.clueminer.utils;

import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public interface Exportable {
    
    public String getName();
    
    public BufferedImage getBufferedImage(int w, int h);
    
    public int getWidth();
    
    public int getHeight();
}
