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
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Sum of squared errors - I_3 from the Zhao 2001 paper
 *
 *
 * @author Andreas De Rijcke
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SumOfSquaredErrors<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "SSE";
    private static final long serialVersionUID = 7246192305561714193L;

    public SumOfSquaredErrors() {
        dm = EuclideanDistance.getInstance();
    }

    public SumOfSquaredErrors(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double sum = 0;
        Cluster clust;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            double tmpSum = 0;
            for (int j = 0; j < clust.size(); j++) {
                for (int k = 0; k < clust.size(); k++) {
                    double error = dm.measure(clust.instance(j), clust.instance(k));
                    tmpSum += error * error;
                }

            }
            sum += tmpSum / clust.size();
        }
        return sum;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // TODO solve bug: score is NaN when clusters with 0 instances
        // should be minimized
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
