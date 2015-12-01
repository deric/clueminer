package org.clueminer.dataset.api;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Plotter<E extends Instance> extends Serializable {

    public void addInstance(E instance);

    public void paint(Graphics g);

    /**
     * Sets the preferred size of this component
     *
     * @param preferredSize
     */
    public void setPreferredSize(Dimension preferredSize);

    /**
     * Sets the minimum size of this component
     *
     * @param minimumSize
     */
    public void setMinimumSize(Dimension minimumSize);

    public int getWidth();

    public int getHeight();

    /**
     * Repaint the component
     */
    public void repaint();

    public void revalidate();

    /**
     * Clear all currently painted data
     */
    public void clearAll();

    /**
     * Set plot title
     * <p>
     * @param title
     */
    public void setTitle(String title);

    /**
     *
     * @param min
     * @param max
     */
    public void setXBounds(double min, double max);

    /**
     *
     * @param min
     * @param max
     */
    public void setYBounds(double min, double max);
}
