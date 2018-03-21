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
package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class NaiveCutoff implements CutoffStrategy {

    public static final String NAME = "naive cutoff";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        return findCutoffNg(hclust);
    }

    /**
     * Search for the highest distance between tree levels (might determine a
     * reasonable number of clusters - simple, but not very precise)
     *
     * @param hclust
     * @return
     */
    private double findCutoffNg(HierarchicalResult hclust) {
        DendroNode current = hclust.getTreeData().getRoot();
        double max = Double.MIN_VALUE;
        double distLeft = findCut(current.getLeft(), max);
        double distRight = findCut(current.getRight(), max);

        if (distLeft > distRight) {
            return distLeft;
        }

        return distRight;
    }

    private double findCut(DendroNode node, double max) {
        double dist = distance(node.getParent(), node);
        double distLeft, distRight;
        if (dist > max) {
            max = dist;
        }

        if (!node.isLeaf()) {
            distLeft = findCut(node.getLeft(), max);
            distRight = findCut(node.getRight(), max);
            if (distLeft > distRight) {
                if (distLeft > max) {
                    max = distLeft;
                }
            } else if (distRight > max) {
                max = distRight;
            }
        }
        return max;
    }

    private double distance(DendroNode parent, DendroNode child) {
        return (parent.getHeight() - child.getHeight()) / 2 + child.getHeight();
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

    @Override
    public boolean isProximityRequired() {
        return false;
    }

}
