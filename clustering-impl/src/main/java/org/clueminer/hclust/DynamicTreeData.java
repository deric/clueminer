package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DynamicTreeData implements DendroTreeData {

    private DendroNode root;

    public DynamicTreeData() {

    }

    @Override
    public int numLeaves() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int treeLevels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numNodes() {
        return root.childCnt();
    }

    @Override
    public DendroNode getRoot() {
        return root;
    }

    @Override
    public void setRoot(DendroNode root) {
        this.root = root;
    }

    @Override
    public DendroNode first() {
        if (root == null) {
            throw new RuntimeException("root is empty");
        }
        DendroNode current = root;
        while (!current.isLeaf()) {
            current = current.getLeft();
        }
        return current;
    }

}
