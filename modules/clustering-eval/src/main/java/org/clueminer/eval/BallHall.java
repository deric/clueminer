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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Sum of squared distances to the centroid (very similar to {@link Deviation})
 *
 * @param <E>
 * @param <C>
 * @cite
 * Ball, Geoffrey H., and David J. Hall. ISODATA, a novel method of data
 * analysis and pattern classification. STANFORD RESEARCH INST MENLO PARK CA,
 * 1965.
 *
 * @see similar to {@link TraceW}, {@link Deviation}, {@link SumOfSquaredErrors}
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class BallHall<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "Ball-Hall";
    private static final long serialVersionUID = -7672134423406888310L;

    public BallHall() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public double score(Clustering<E, C> clusters, Props params) {
        double sum = 0;
        Cluster<E> clust;
        double error, tmpSum;
        E centroid;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            tmpSum = 0;
            centroid = clust.getCentroid();
            for (int j = 0; j < clust.size(); j++) {
                error = dm.measure(clust.instance(j), centroid);
                tmpSum += error * error;
            }
            sum += tmpSum / clust.size();
        }

        return sum / clusters.size();
    }

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
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
