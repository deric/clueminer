/*
 * Copyright (C) 2011-2017 clueminer.org
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

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Tomas Barton
 */
public interface DendroNode {

    /**
     *
     * @return true when is a leaf node
     */
    boolean isLeaf();

    /**
     *
     * @return true when node is top of a dendrogram tree
     */
    boolean isRoot();

    /**
     *
     * @return left node if any, otherwise null
     */
    DendroNode getLeft();

    /**
     * Set given node on left branch of current node
     *
     * @param left
     * @return the left node (fluent interface)
     */
    DendroNode setLeft(DendroNode left);

    /**
     *
     * @return true when left node exists
     */
    boolean hasLeft();

    /**
     *
     * @return right node if any, otherwise null
     */
    DendroNode getRight();

    /**
     * Set given node on right branch of current node
     *
     * @param right
     * @return the right node (fluent interface)
     */
    DendroNode setRight(DendroNode right);

    /**
     *
     * @return true when right node exists
     */
    boolean hasRight();

    /**
     * (Sub)tree level
     *
     * @return number of levels under this node
     */
    int level();

    /**
     * In case that tree construction is finished, we can cache tree levels
     *
     * @param level
     */
    void setLevel(int level);

    /**
     * If root node, parent is null
     *
     * @return parent node
     */
    DendroNode getParent();

    void setParent(DendroNode parent);

    /**
     *
     * @return number of children
     */
    int childCnt();

    double getHeight();

    /**
     *
     * @param height
     * @return this node (fluent interface)
     */
    DendroNode setHeight(double height);

    /**
     * Position of last node is also width of the tree
     *
     * @return width of tree
     */
    double getPosition();

    /**
     *
     * @param value
     * @return this node (fluent interface)
     */
    DendroNode setPosition(double value);

    /**
     * set id in mapping (usually from left to right, 0 to n-1)
     *
     * @param id
     * @return this node
     */
    DendroNode setId(int id);

    /**
     * id in mapping array
     *
     * @return
     */
    int getId();

    /**
     * Mapping index, used for mapping to Instances or Attributes
     *
     * @return
     */
    int getIndex();

    /**
     * printing helper
     *
     * @param out
     * @param isRight
     * @param indent
     * @throws IOException
     */
    void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException;

    /**
     * printing helper
     *
     * @param out
     * @param isRight
     * @param indent
     * @throws IOException
     */
    void printTreeWithHeight(OutputStreamWriter out, boolean isRight, String indent) throws IOException;

    /**
     * minimum distance in subtree - used for ordering dendrogram
     *
     * @param min
     */
    void setMin(double min);

    /**
     *
     * @return minimum distance in a subtree
     */
    double getMin();

    /**
     * Swap leaf and right children nodes. After this operation whole tree needs
     * to update mapping.
     */
    void swapChildren();
}
