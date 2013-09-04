    package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DTreeLeaf extends DTreeNode implements DendroNode {

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public DendroNode getLeft() {
        throw new RuntimeException("Invalid operation. Leaf can't have childern nodes."); 
    }

    @Override
    public boolean hasLeft() {
        return false;
    }

    @Override
    public DendroNode getRight() {
        throw new RuntimeException("Invalid operation. Leaf can't have childern nodes."); 
    }

    @Override
    public boolean hasRight() {
        return false;
    }

    @Override
    public double height() {
        return 0.0;
    }
    
    @Override
    public int childCnt(){
        return 0;
    }

}
