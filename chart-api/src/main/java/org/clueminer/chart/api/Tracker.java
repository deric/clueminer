package org.clueminer.chart.api;

/**
 *
 * @author Tomas Barton
 */
public interface Tracker {
    
    public void setIndex(int i);
    
    public void labelText();
    
    public void repaint();
    
    public void moveLeft();
    
    public void moveRight();

}
