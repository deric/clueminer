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
package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class NaiveCutoffByLevel implements CutoffStrategy {

    public static final String NAME = "Naive cutoff by level";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {

        return findCutoffLevel(hclust);

    }

    public double findCutoffLevel(HierarchicalResult hclust) {
        double height = 0;
        double max = 0;
        double upper;
        double lower = hclust.cutTreeByLevel(0);
        for (int i = 1; i < hclust.treeLevels() + 1; i++) {
            upper = hclust.getHeightByLevel(i);
            if (upper - lower >= max) {
                max = upper - lower;
                height = (upper - lower) / 2.0 + lower;
            }
            lower = upper;
        }
        return height;
    }

    private double distance(DendroNode parent, DendroNode child) {
        return (parent.getHeight() - child.getHeight()) / 2 + child.getHeight();
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

}
