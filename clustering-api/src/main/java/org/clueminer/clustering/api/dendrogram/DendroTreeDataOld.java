package org.clueminer.clustering.api.dendrogram;

/**
 *
 * @author Tomas Barton
 */
public interface DendroTreeDataOld extends DendroTreeData {

    /**
     * @param idx
     * @return node index of tree levels
     */
    int getOrder(int idx);

    /**
     * @param idx
     * @return tree levels at @idx level
     */
    double getHeight(int idx);

    /**
     * Array size is 2*level
     *
     * @param idx
     * @return index in level array
     */
    int getLeft(int idx);

    /**
     * Array size is 2*level if level(right[idx]) == 0 => node is leaf
     *
     * @param idx
     * @return index in level array
     */
    int getRight(int idx);

    /**
     *
     * @param idx
     * @return true when node at given index is a leaf
     */
    boolean isLeaf(int idx);
}
