package org.clueminer.dgram.eval;

import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class OptimalTreeOrder {

    public void optimize(HierarchicalResult clustering) {
        System.out.println("tree order");
        System.out.println("similarity matrix");
        clustering.getProximityMatrix().printLower(2, 2);
        DendroTreeData tree = clustering.getTreeData();
        numberNodes(tree.getRoot(), 0, 0);
        //tree.print();
        //Dump.array(tree.getMapping(), "tree mapping");
//        optOrder(tree.getRoot(), clustering.getProximityMatrix());
        System.out.println("printing canonical tree");
        tree.printCanonical();
    }

    /**
     * Number leaves from 0 to {numNodes}
     *
     * @param node
     * @return
     */
    public void numberNodes(DendroNode node, int label, int level) {
        if (!node.isLeaf()) {
            if (node.hasLeft()) {
                numberNodes(node.getLeft(), label << 1, level + 1);
            }
            if (node.hasRight()) {
                numberNodes(node.getRight(), (label << 1) + 1, level + 1);
            }
        }
        node.setLabel(label);
        node.setLevel(level);
    }

    public void optOrder(DendroNode node, Matrix similarity) {

    }

    public void treeOrder(DendroNode v, DendroNode u, DendroNode r) {

    }

}
