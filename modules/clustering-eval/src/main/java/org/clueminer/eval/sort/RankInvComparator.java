/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.eval.sort;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Instance;

/**
 * Bigger rank, better result
 *
 * @author deric
 */
public class RankInvComparator<E extends Instance, C extends Cluster<E>> extends RankComparator<E, C> implements ClusterEvaluation<E, C> {

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public int compare(double s1, double s2) {
        if (s1 > s2) {
            return 1;
        } else if (s1 == s2) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0.0;
    }

    @Override
    public double getMax() {
        return Double.MAX_VALUE;
    }

}
