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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Instance;

/**
 * Ensures comparing between standard Double values and not finite values (NaN,
 * positive infinity, negative infinity)
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractComparator<E extends Instance, C extends Cluster<E>> implements ClusterEvaluation<E, C> {

    protected static final double EPS = 1e-8;

    /**
     * 0 when arguments are the same (within EPS range). 1 when score1 is worser
     * than score2. -1 when score1 is better than score2. This behaviour is
     * inverse to Java defaults because we use descending order by default
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public int compare(double score1, double score2) {
        if (!isFinite(score1)) {
            score1 = replaceNaN(score1);
        }
        if (!isFinite(score2)) {
            score2 = replaceNaN(score2);
        }

        if (Math.abs(score1 - score2) < EPS) {
            return 0;
        }
        if (isMaximized()) { //descending order [10, 9, 8, ...]
            if (score1 < score2) {
                return 1;
            }
        } else { // ascending order [1, 2, 3]
            if (score1 > score2) {
                return 1;
            }
        }
        return -1;
    }

    /**
     * NaN should be considered as the worst value. Replaces NaN and infinity
     * values.
     *
     * @param v
     * @return
     */
    private double replaceNaN(double v) {
        if (Double.isNaN(v)) {
            if (isMaximized()) {
                return Double.MIN_VALUE;
            } else {
                return Double.MAX_VALUE;
            }
        } else {
            if (v == Double.NEGATIVE_INFINITY) {
                return Double.MIN_VALUE;
            } else { // POSITIVE_INFINITY
                return Double.MAX_VALUE;
            }
        }
    }

    /**
     * Could be replace by Double.isFinite which is available in Java 8
     *
     * @param d
     * @return
     */
    public boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

}
