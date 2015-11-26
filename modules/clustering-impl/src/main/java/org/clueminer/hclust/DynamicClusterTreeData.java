package org.clueminer.hclust;

import java.util.ArrayList;
import java.util.Stack;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Bruna
 */
public class DynamicClusterTreeData extends DynamicTreeData {

    int i;

    /**
     * When given expected size we might avoid reallocation of memory
     *
     * @param root
     * @param hintSize estimated number number of nodes (doesn't have to be
     * accurate)
     */
    public DynamicClusterTreeData(DendroNode root, int hintSize) {
        super(root, hintSize);
    }

    public DynamicClusterTreeData(DendroNode root) {
        super(root);
    }

    /**
     * In-order tree walk to mark default order of instances. There are clusters
     * saved in leaves and all items inside clusters are mapped.
     *
     * @param n
     * @param node - root of the tree
     * @return
     */
    @Override
    public int[] createMapping(int n, DendroNode node) {
        return createMapping(n, node, null);
    }

    @Override
    public int[] createMapping(int n, DendroNode node, DendroNode noise) {
        Stack<DendroNode> stack = new Stack<>();
        i = 0;
        int leavesCounter = 0;
        ensureCapacity(n);
        while (!stack.isEmpty() || node != null) {
            if (node != null) {
                stack.push(node);
                node = node.getLeft();
            } else {
                node = stack.pop();
                if (node.isLeaf()) {
                    mapLeaf(node, leavesCounter++);
                }
                node = node.getRight();
            }
        }

        if (noise != null) {
            mapLeaf(noise, leavesCounter++);
        }

        ensureCapacity(i);
        return mapping;
    }

    private void mapLeaf(DendroNode node, int leavesCounter) {
        resizeIfNeeded(i);
        leaves[leavesCounter++] = node;
        DClusterLeaf leaf = (DClusterLeaf) node;
        ArrayList<Instance> instances = (ArrayList<Instance>) leaf.getInstances();
        node.setPosition(i + instances.size() / 2);
        //Map all nodes in cluster
        for (Instance instance : instances) {
            mapping[i] = instance.getIndex();
            i++;
            resizeIfNeeded(i);
            //System.out.println((i - 1) + " -> " + mapping[(i - 1)]);
        }
    }

    private void resizeIfNeeded(int i) {
        if (i >= mapping.length) {
            int req = (int) (i * 1.618);
            if (req <= i) {
                req = i + 1;
            }
            ensureCapacity(req);
        }
    }

    @Override
    public boolean containsClusters() {
        return true;
    }

}
