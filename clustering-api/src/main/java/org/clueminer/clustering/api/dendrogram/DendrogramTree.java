package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Dendrogram tree renderer interface
 *
 * @author Tomas Barton
 */
public interface DendrogramTree extends DendrogramDataListener {

    public void paint(Graphics g);

    public void updateSize();

    public int getMinDistance();

    public int getMaxDistance();

    public void setTreeData(DendroTreeData treeData);

    public DendroTreeData getTreeData();

    public Dimension getSize();

    public void addTreeListener(TreeListener listener);

    public void removeTreeListener(TreeListener listener);

    public boolean hasData();

    public void fireTreeUpdated();

    public int getWidth();

    public int getHeight();

    /**
     * Moves and resizes this component. The new location of the top-left
     * corner is specified by <code>x</code> and <code>y</code>, and the
     * new size is specified by <code>width</code> and <code>height</code>.
     * <p>
     * This method changes layout-related information, and therefore,
     * invalidates the component hierarchy.
     *
     * @param x      the new <i>x</i>-coordinate of this component
     * @param y      the new <i>y</i>-coordinate of this component
     * @param width  the new <code>width</code> of this component
     * @param height the new <code>height</code> of this
     *               component
     */
    public void setBounds(int x, int y, int width, int height);

    /**
     *
     * @return minimal tree distance
     */
    public double getMinTreeHeight();

    public double getMidTreeHeight();

    public double getMaxTreeHeight();

}
