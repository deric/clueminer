package org.clueminer.gui;

import java.awt.Color;

/**
 *
 * @author Tomas Barton
 */
public interface ColorPalette {
    
    public void setRange(double min, double max);

    public Color getColor(double value);
    
    /**
     * 
     * @return min value in interval
     */
    public double getMin();
    
    /**
     * 
     * @return max value in the displayed interval
     */
    public double getMax();
    
    /**
     * 
     * @return middle value in the displayed interval
     */
    public double getMid();
   
}
