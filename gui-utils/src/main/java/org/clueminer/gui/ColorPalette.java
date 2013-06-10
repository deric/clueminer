package org.clueminer.gui;

import java.awt.Color;

/**
 *
 * @author Tomas Barton
 */
public interface ColorPalette {
    
    public void setRange(double min, double max);

    public Color getColor(double value);
   
}
