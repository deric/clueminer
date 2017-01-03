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
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 * @param <T>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class BanfieldRaftery<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "Banfield-Raftery";
    private static final long serialVersionUID = -6474798417487400001L;

    public BanfieldRaftery() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double score = 0.0;
        double tmp;

        for (C clust : clusters) {
            tmp = sumOfSquaredError(clust) / clust.size();
            //avoid undefined expressions
            if (tmp > 0) {
                score += clust.size() * Math.log(tmp);
            }
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
