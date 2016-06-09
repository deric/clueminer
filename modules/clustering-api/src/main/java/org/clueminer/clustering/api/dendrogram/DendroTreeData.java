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

/**
 * Dendrogram tree structure
 *
 * @author Tomas Barton
 */
public interface DendroTreeData {

    /**
     * Return number of terminal nodes (leaves)
     *
     * @return number of tree leaves
     */
    int numLeaves();

    /**
     * Total number of tree nodes including leaves.
     *
     * @return
     */
    int numNodes();

    /**
     *
     * @return number of levels in tree
     */
    int treeLevels();

    /**
     * Number of distinct node heights within a tree
     *
     * @param tolerance threshold in height difference for considering height as
     * the same
     * @return
     */
    int distinctHeights(double tolerance);

    /**
     * Distinct levels with default tolerance
     *
     * @return number of distinct tree levels within a tree (without leaves)
     */
    int distinctHeights();

    /**
     *
     * @return tree node
     */
    DendroNode getRoot();

    /**
     * Set node as tree root
     *
     * @param root
     * @return itself
     */
    DendroNode setRoot(DendroNode root);

    /**
     * Left-most leaf
     *
     * @return first leaf
     */
    DendroNode first();

    /**
     * Create mapping of leaves
     *
     * @param n
     * @param node - root
     * @return
     */
    int[] createMapping(int n, DendroNode node);

    /**
     * Create mapping of leaves and map one leaf as noise
     *
     * @param n
     * @param node - root
     * @param noise leaf with noise
     * @return
     */
    int[] createMapping(int n, DendroNode node, DendroNode noise);

    /**
     * Print tree to stdout
     */
    void print();

    /**
     * Print tree to stdout, nodes are printed in levels according to their
     * heights. Heights should be integers otherwise levels are not printed
     * correctly.
     */
    void printWithHeight();

    /**
     * Print tree with given root
     *
     * @param treeRoot
     */
    void print(DendroNode treeRoot);

    /**
     * Set mapping to instances indexes
     *
     * @param mapping
     */
    void setMapping(int[] mapping);

    /**
     * Return mapping of leaves to indexes in dataset
     *
     * @return
     */
    int[] getMapping();

    /**
     * Mapped index
     *
     * @param i
     * @return instance index
     */
    int getMappedId(int i);

    /**
     * Check whether mapping was set
     *
     * @return true when mapping to actual tree order exists
     */
    boolean hasMapping();

    /**
     *
     * @param i th leaf
     * @return
     */
    DendroNode getLeaf(int i);

    /**
     * Set leaf at given position
     *
     * @param i
     * @param node
     */
    void setLeaf(int i, DendroNode node);

    /**
     * Set array of leaves nodes
     *
     * @param leaves
     */
    void setLeaves(DendroNode[] leaves);

    /**
     * Update precomputed position of the node
     *
     * @param node
     * @return
     */
    double updatePositions(DendroNode node);

    /**
     * Whether the tree leaves contain clusters.
     *
     * @return
     */
    public boolean containsClusters();

}
