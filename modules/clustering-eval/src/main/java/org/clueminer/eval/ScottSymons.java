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
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class ScottSymons extends AbstractEvaluator implements InternalEvaluator {

    private static final String name = "Scott-Symons";
    private static final long serialVersionUID = 8534361956815910728L;

    public ScottSymons() {
        dm = EuclideanDistance.getInstance();
    }

    public ScottSymons(Distance distance) {
        this.dm = distance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double score = 0.0;
        for (Cluster clust : clusters) {
            score += clust.size() * Math.log(wgScatter(clust).times(1.0 / (double) clust.size()).det());
        }

        return score;
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
