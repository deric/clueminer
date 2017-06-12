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
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Method for finding largest gap in the dendrogram tree.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class FirstJump implements CutoffStrategy {

    public static final String NAME = "FirstJump";

    // Values with best average performance found by several experiments
    // Values 100 and 2 give much better results on specific datasets but not on average
    private int start = 340;
    private double factor = 1.91;

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Look for first jump several times higher than the average. If no jump
     * exists, decrease the threshold and repeat.
     *
     * @param hclust
     * @return
     */
    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        double result = 0;
        double average = computeAverageHeight(hclust);
        for (int i = start; i >= 0; i /= factor) {
            result = findFirstJump(hclust, average * i);
            if (result != 0) {
                return result;
            }
        }

        return result;
    }

    /**
     * Find first jump bigger than the given size in the upper half of the
     * dendrogram.
     *
     * @param hclust
     * @param jumpHeight size of the jump
     * @return height of the first jump or 0 if not found
     */
    private double findFirstJump(HierarchicalResult hclust, double jumpHeight) {
        double upper;
        double lower = hclust.getHeightByLevel(hclust.treeLevels() / 2 - 1);
        for (int i = hclust.treeLevels() / 2; i < hclust.treeLevels() + 1; i++) {
            upper = hclust.getHeightByLevel(i);

            if (upper - lower > jumpHeight) {
                return lower + (upper - lower) / 2;
            }

            lower = upper;
        }
        return 0;
    }

    /**
     * Find average distance in the first half of levels.
     *
     * @param hclust
     * @return
     */
    private double computeAverageHeight(HierarchicalResult hclust) {
        double sum = 0;
        double upper;
        double lower = hclust.getHeightByLevel(0);
        for (int i = 1; i <= hclust.treeLevels() / 2; i++) {
            upper = hclust.getHeightByLevel(i);
            sum += upper - lower;
            lower = upper;
        }
        if (sum == 0) {
            return 1;
        }
        return sum / (hclust.treeLevels() / 2);
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setFactor(double factor) {
        this.factor = factor;
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
