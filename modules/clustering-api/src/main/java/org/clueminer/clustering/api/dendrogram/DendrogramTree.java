/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.api.dendrogram;

import java.awt.Dimension;
import org.clueminer.clustering.api.HierarchicalResult;

/**
 * Dendrogram tree renderer interface
 *
 * @author Tomas Barton
 */
public interface DendrogramTree extends DendrogramDataListener {

    /**
     * Calculate component size
     */
    void recalculate();

    /**
     * Distance in pixels from one side of tree to another
     *
     * @return
     */
    int getTreeWidth();

    /**
     * Height of rendered tree in pixels
     *
     * @return
     */
    int getTreeHeight();

    void setTreeData(DendroTreeData treeData);

    DendroTreeData getTreeData();

    Dimension getRealSize();

    void addTreeListener(TreeListener listener);

    void removeTreeListener(TreeListener listener);

    boolean hasData();

    void fireTreeUpdated();

    int getWidth();

    int getHeight();

    /**
     * Moves and resizes this component. The new location of the top-left corner
     * is specified by <code>x</code> and <code>y</code>, and the new size is
     * specified by <code>width</code> and <code>height</code>.
     * <p>
     * This method changes layout-related information, and therefore,
     * invalidates the component hierarchy.
     *
     * @param x the new <i>x</i>-coordinate of this component
     * @param y the new <i>y</i>-coordinate of this component
     * @param width the new <code>width</code> of this component
     * @param height the new <code>height</code> of this component
     */
    void setBounds(int x, int y, int width, int height);

    /**
     * Zero tree height
     *
     * @return minimal tree distance
     */
    double getMinTreeHeight();

    double getMidTreeHeight();

    /**
     * Distance which represents root of the tree
     *
     * @return
     */
    double getMaxTreeHeight();

    /**
     * Triggered when leaf order was optimized
     *
     * @param source
     * @param result
     */
    void fireLeafOrderUpdated(Object source, HierarchicalResult result);

    /**
     * Horizontal tree are typically used for rows selection
     *
     * @return true when tree has horizontal selection
     */
    boolean isHorizontal();

    /**
     * Vertical tree typically selects columns
     *
     * @return
     */
    boolean isVertical();

}
