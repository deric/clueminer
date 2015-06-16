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
package org.clueminer.evaluation.inline;

import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author deric
 */
public class DominanceComparator {

    private final double epsilon = 0.0;

    public int compare(Clustering c1, Clustering c2, List<ClusterEvaluation> objectives) {

        int result;
        boolean solution1Dominates = false;
        boolean solution2Dominates = false;

        int flag;
        double value1, value2;
        for (ClusterEvaluation objective : objectives) {
            value1 = objective.score(c1);
            value2 = objective.score(c2);
            if (value1 / (1 + epsilon) < value2) {
                flag = -1;
            } else if (value1 / (1 + epsilon) > value2) {
                flag = 1;
            } else {
                flag = 0;
            }
            if (flag == -1) {
                solution1Dominates = true;
            }
            if (flag == 1) {
                solution2Dominates = true;
            }
        }

        if (solution1Dominates == solution2Dominates) {
            // non-dominated solutions
            result = 0;
        } else if (solution1Dominates) {
            // solution1 dominates
            result = -1;
        } else {
            // solution2 dominates
            result = 1;
        }
        return result;

    }

}
