/*
 * Copyright (C) 2011-2018 clueminer.org
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
            //      System.out.println((i - 1) + " -> " + mapping[(i - 1)]);
        }
    }

    @Override
    public boolean containsClusters() {
        return true;
    }

}
