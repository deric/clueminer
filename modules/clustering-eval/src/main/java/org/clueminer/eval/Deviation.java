/*
 * Copyright (C) 2015 clueminer.org
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
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Deviation is meant for multi-objective optimization, using it as single
 * objective is not recommended. It would produce too many compact clusters
 * (with minimal distance to centroid).
 *
 * @param <E>
 * @param <C>
 * @see Handl, Julia, and Joshua Knowles. "An evolutionary approach to
 * multiobjective clustering." Evolutionary Computation, IEEE Transactions on
 * 11.1 (2007): 56-76.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Deviation<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = -1456624325537837873L;
    private static final String NAME = "Deviation";

    public Deviation() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double sum = 0;
        Cluster clust;
        double error, tmpSum;
        Instance centroid;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            tmpSum = 0;
            centroid = clust.getCentroid();
            for (int j = 0; j < clust.size(); j++) {
                error = dm.measure(clust.instance(j), centroid);
                tmpSum += error;
            }
            sum += tmpSum / clust.size();
        }
        return sum;
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
