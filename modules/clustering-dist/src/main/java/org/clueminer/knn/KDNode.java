/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.knn;

/**
 * The root in the KD-tree.
 *
 * @author deric
 */
class KDNode {

    /**
     * Number of dataset stored in this node.
     */
    int count;
    /**
     * The smallest point index stored in this node.
     */
    int index;
    /**
     * The index of coordinate used to split this node.
     */
    int split;
    /**
     * The cutoff used to split the specific coordinate.
     */
    double cutoff;
    /**
     * The child node which values of split coordinate is less than the cutoff
     * value.
     */
    KDNode lower;
    /**
     * The child node which values of split coordinate is greater than or equal
     * to the cutoff value.
     */
    KDNode upper;

    /**
     * If the node is a leaf node.
     */
    boolean isLeaf() {
        return lower == null && upper == null;
    }
}
