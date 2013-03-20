package org.clueminer.dataset.api;

import java.awt.Color;
import java.io.Serializable;

/**
 * Should be used for generating color schemes and colors for nice
 * visualizations, generated colors are expected to be as different as possible.
 *
 * @author Tomas Barton
 */
public interface ColorGenerator extends Serializable{

    public Color next();
    
    /**
     * Generate color based on the previous color
     * @param base - color on which will be the result based
     * @return 
     */
    public Color next(Color base);
}
