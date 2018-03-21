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
 * In statistics the point-biserial coefficient is a correlation measure between
 * a continuous variable A and a binary variable B.
 *
 * Adapted from point biserial correlation evaluator (Brogden 1949)
 *
 * @param <E>
 * @param <C>
 * @cite Mitligan, G. W. (1981a). A Monte Carlo study of thirty internal
 * criterion measures for cluster analysis. Psychometrika, 46, 187-199.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class PointBiserial<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "PointBiserial";
    private static final long serialVersionUID = -3222061698654228829L;

    public PointBiserial() {
        dm = EuclideanDistance.getInstance();
    }

    public PointBiserial(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Simplified computation of PointBiserial index
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double nw = numW(clusters);
        double nt = numT(clusters);
        double nb = nt - nw;
        double sw = 0.0, sb;

        //sum of within cluster distances
        for (Cluster<E> clust : clusters) {
            sw += sumWithin(clust);
        }
        //sum of between cluster distances
        sb = sumBetween(clusters);

        return (sw / nw - sb / nb) * Math.sqrt(nw * nb) / nt;
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
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }
}
