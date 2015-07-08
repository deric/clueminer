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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class Compactness extends AbstractEvaluator {

    private static final String NAME = "Compactness";
    private static final long serialVersionUID = -6033217683756447290L;

    public Compactness() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double sum = 0;
        Cluster clust;
        double dist, tmpSum;
        Instance a, b;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            tmpSum = 0;
            for (int j = 0; j < clust.size(); j++) {
                a = clust.get(j);
                //TODO: fully implement
                for (int k = 0; k < j; k++) {
                    b = clust.get(k);
                    tmpSum += dm.measure(a, b);
                }
            }
            sum += tmpSum / clust.size();
        }
        return sum / clusters.instancesCount();
    }

    /**
     * Should be minimized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Best value
     *
     * @return
     */
    @Override
    public double getMax() {
        return 0;
    }

}
